package com.conleos.views.form;

import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import java.time.Year;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Edit your Form")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout {

    List<TextArea> descriptions = new ArrayList<>();
    Select<Integer> nr = new Select<>();

    public FormView() {
        createContent();
    }

    private void createContent() {

        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        H1 h1 = new H1("Ausbildungsnachweis");

        header.add(h1);

        nr.setLabel("Nachweis Nr.:");
        nr.setItems(new nachweisSelect().nachweisNr);
        nr.setValue(1);
        H2 h2 = new H2("Aufgaben:");
        Hr hr = new Hr();
        add(header,nr,hr,h2);

        LocalDate beginOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; i < 5; i++) {
            createFormContentForDay(beginOfWeek.plusDays(i));

        }

        Button saveBtn = new Button("Save");
        saveBtn.addClickListener(save -> {
            for (TextArea description : descriptions) {
                Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
                Form form = new Form(session.getUser(),nr.getValue(),description.getValue());
                FormService.getInstance().saveForm(form);
            }
        });
        add(saveBtn);
    }

    private void createFormContentForDay(LocalDate date) {
        String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, UI.getCurrent().getLocale());

        VerticalLayout container = new VerticalLayout();

        Button addBtn = new Button("Add", VaadinIcon.PLUS.create());
        VerticalLayout day = new VerticalLayout();
        day.setClassName(LumoUtility.Border.ALL);
        TextField timeSum = new TextField();
        timeSum.setLabel("Zeit gesamt:");

        addBtn.addClickListener(event -> {
            Select<KindOfWork> select = new Select<>();
            select.setLabel("Art");
            select.setItems(KindOfWork.PracticalWork, KindOfWork.Schooling);
            select.setValue(KindOfWork.PracticalWork);
            TextArea time = new TextArea("Zeit");
            time.setWidth("60px");
            time.setHeight("60px");
            time.addValueChangeListener(timeChange -> {
                List<Component> components = container.getChildren().toList();
                double sum = 0;
                for (Component component : components) {
                    if (component instanceof TextField && component.getId().isPresent() && component.getId().get().equals("time")) {
                        try {
                            Double zeit = Double.parseDouble(((TextField) component).getValue());
                            System.out.println(zeit);
                            sum += zeit;
                        } catch (NumberFormatException e) {
                            System.out.println("Fehler bei Zeitwert.");
                        }
                    }
                }
                timeSum.setValue(String.valueOf(sum));
            });
            TextArea area = new TextArea("Beschreibung");
            descriptions.add(area);
            Button delBtn = new Button(VaadinIcon.CLOSE.create());
            delBtn.addClickListener(eventDel -> {
                Component parent = delBtn.getParent().get();
                if (parent instanceof HorizontalLayout) {
                    container.remove(parent);
                }
            });
            container.add(new HorizontalLayout(select, time, area,delBtn));
        });

        day.add(new Span(dayLabel), container, addBtn, timeSum);
        add(day);
    }

}
