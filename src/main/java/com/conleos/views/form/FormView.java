package com.conleos.views.form;

import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.Year;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Edit your Form")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout {

    List<TextArea> descriptions = new ArrayList<>();
    Select<Integer> nr = new Select<>();

    public FormView() {
        createContent();
    }

    private void createContent() {

        nr.setLabel("Nachweis Nr.:");
        nr.setItems(new nachweisSelect().nachweisNr);
        nr.setValue(1);
        HorizontalLayout yearWeek = new HorizontalLayout();
        Select<Year> year = new Select<>();
        year.setLabel("Jahr");
        year.setItems(new YearSelect().years);
        year.setValue(Year.now());
        Select<Integer> kw = new Select<>();
        kw.setLabel("Kalenderwoche");
        kw.setItems(new Kw().calendarWeeks);
        kw.setValue(1);
        yearWeek.add(year,kw);

        add(nr,yearWeek);

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

        addBtn.addClickListener(event -> {
            Select<KindOfWork> select = new Select<>();
            select.setLabel("Art");
            select.setItems(KindOfWork.PracticalWork, KindOfWork.Schooling);
            select.setValue(KindOfWork.PracticalWork);
            TextArea time = new TextArea("Zeit");
            time.setWidth("60px");
            time.setHeight("60px");
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

        add(new Span(dayLabel), container, addBtn);
    }

}
