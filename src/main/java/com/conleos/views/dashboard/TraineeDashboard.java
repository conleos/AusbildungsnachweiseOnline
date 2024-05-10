package com.conleos.views.dashboard;

import com.conleos.common.ColorGenerator;
import com.conleos.common.FormUtil;
import com.conleos.common.HtmlColor;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.pdf.PdfDownloadButton;
import com.conleos.views.form.DateBasedNavigator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TraineeDashboard extends Main implements HasComponents, HasStyle {

    private OrderedList itemContainer;

    private DashboardFormFilter filter;

    public TraineeDashboard(ArrayList<Component> headerComponents, User trainee) {

        headerComponents.add(new DateBasedNavigator(trainee, LocalDate.now()));
        headerComponents.add(PdfDownloadButton.create(trainee));

        LocalDate beginOfCurrentWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        if (trainee.getStartDate() == null) {
            Span span = new Span("Oops! Someone forgot to set your 'Begin of Work' Date!");
            span.getElement().getThemeList().add("badge error");
            add(span);
            return;
        }
        LocalDate beginOfWork = trainee.getStartDate().with(DayOfWeek.MONDAY);

        if (beginOfCurrentWeek.isBefore(beginOfWork)) {
            Span span = new Span("Work begins " + trainee.getStartDate().toString());
            span.getElement().getThemeList().add("badge error");
            add(span);
            return;
        }

        constructUI(trainee);

        generateFormCards(trainee);
    }

    private void generateFormCards(User trainee) {
        itemContainer.removeAll();

        LocalDate beginOfCurrentWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate beginOfWork = trainee.getStartDate().with(DayOfWeek.MONDAY);

        List<FormCard> cards = new ArrayList<>();
        int formNumber = 1;
        for (LocalDate It = beginOfWork; It.isBefore(beginOfCurrentWeek) || It.equals(beginOfCurrentWeek); It = It.plusWeeks(1)) {
            cards.add(new FormCard(It, trainee, formNumber++));
        }

        // Sorting and filtering of cards right here
        filter.filterAndSortFormCards(cards);

        for (FormCard card : cards) {
            itemContainer.add(card);
        }
    }
    private void constructUI(User trainee) {
        addClassNames("dashboard-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Dashboard");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Manage your Forms here.");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        itemContainer = new OrderedList();
        itemContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        filter = new DashboardFormFilter();
        filter.getApplyFiltersButton().addClickListener(event -> {
            generateFormCards(trainee);
        });

        container.add(headerContainer, filter.getButton(), filter.getDialog());
        add(container, itemContainer);
    }

}

class FormCard extends ListItem {

    public FormCard(LocalDate date, User trainee, int formNumber) {
        addClassNames(LumoUtility.Background.CONTRAST_5, Display.FLEX, LumoUtility.FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        getStyle().set("cursor", "pointer");

        Div div = new Div();
        div.addClassNames(LumoUtility.Background.CONTRAST_20, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
        div.setHeight("160px");

        div.add(createCalendar(date, formNumber));
        HtmlColor backgroundColor = HtmlColor.from(ColorGenerator.fromRandomString("" + formNumber).darker().darker());
        HtmlColor backgroundColor2 = HtmlColor.from(backgroundColor.toAWTColor().darker());
        div.getStyle().set("background-image", "repeating-linear-gradient(45deg, " + backgroundColor + ", " + backgroundColor + " 10px, " + backgroundColor2 + " 10px, " + backgroundColor2 + " 20px)");

        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
        header.setText(DateTimeFormatter.ofPattern("dd. MMMM uuuu", Locale.GERMAN).format(date));

        Span subtitle = new Span();
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subtitle.setText("#" + formNumber);

        Paragraph description = new Paragraph("");
        description.addClassName(Margin.Vertical.MEDIUM);

        Span badge = FormUtil.createFormBadge(FormService.getInstance().getFormByDateAndUser(date, trainee));

        add(div, header, subtitle, description, badge);

        addClickListener(listItemClickEvent -> {
            Form form = FormService.getInstance().getFormByDateAndUser(date, trainee);
            if (form == null) {
                form = new Form(trainee, date);
            }
            FormService.getInstance().saveForm(form);
            UI.getCurrent().navigate("/form/" + form.getId());
        });
    }

    private Component createCalendar(LocalDate beginDate, int formNumber) {
        LocalDate endDate = beginDate.plusDays(6);
        VerticalLayout layout = new VerticalLayout();

        LocalDate firstDayOfMonth = beginDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = beginDate.withDayOfMonth(beginDate.getMonth().length(beginDate.isLeapYear()));

        LocalDate firstMondayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY);

        // MONTH YEAR
        Span monthYear = new Span(beginDate.getMonth().getDisplayName(TextStyle.FULL, UI.getCurrent().getLocale()) + " " + beginDate.getYear());
        monthYear.getElement().getThemeList().add("badge contrast");
        monthYear.getStyle().set("background-color", HtmlColor.from(ColorGenerator.fromRandomString("" + formNumber)).toString());
        layout.add(monthYear);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("margin", "1px");

        LocalDate iterDate = firstMondayOfCalendar;
        int iterDayOfTheMonth = 1;

        for (int i = 0; i < 6; i++) {
            HorizontalLayout container = new HorizontalLayout();
            container.setAlignItems(FlexComponent.Alignment.CENTER);
            container.setSpacing(false);
            container.getStyle().set("margin", "1px");

            for (int j = 0; j < 7; j++) {
                Span day = new Span("00");
                day.getElement().getThemeList().add("badge contrast");

                Color color = ColorGenerator.fromRandomString("" + formNumber);

                if (iterDate.isBefore(firstDayOfMonth) || iterDate.isAfter(lastDayOfMonth)) {
                    color = color.darker().darker();
                    day.setText("  ");
                } else {
                    day.setText(String.valueOf(iterDayOfTheMonth++));
                }

                if (iterDate.isEqual(beginDate) || iterDate.isEqual(endDate) || (iterDate.isAfter(beginDate) && iterDate.isBefore(endDate))) {
                    color = Color.WHITE;
                }

                day.getStyle().set("background-color", HtmlColor.from(color).toString());

                day.setWidth("18px");
                day.setHeight("18px");
                container.add(day);

                iterDate = iterDate.plusDays(1);
            }
            layout.add(container);
        }


        return layout;
    }

}