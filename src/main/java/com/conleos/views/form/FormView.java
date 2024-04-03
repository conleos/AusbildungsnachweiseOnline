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
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.time.Year;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Edit your Form")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout implements HasUrlParameter<Long> {

    ArrayList<Day> days = new ArrayList<>();
    Select<Integer> nr = new Select<>();

    public FormView() {

    }

    private void createContent(Form form) {

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

        for (int i = 0; i < 5; i++) {
            Day day = new Day(i);
            days.add(day);
            add(day.createFormContentForDay(form));
        }

        Button saveBtn = new Button("Save");
        saveBtn.addClickListener(save -> {
            form.removeAllEntries();
            for (Day day : days) {
                form.addEntries(day.getEntries(form));
            }
            FormService.getInstance().saveForm(form);
        });
        add(saveBtn);
    }


    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        Form form = FormService.getInstance().getFormByID(parameter);

        if (form == null) {
            add(new Span("Form not found!"));
            return;
        }

        User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();

        switch (user.getRole()) {
            case Admin -> {
                createContent(form);
            }
            case Trainee -> {
                if (form.getOwner().getId() == user.getId()) {
                    createContent(form);
                } else {
                    add(new Span("access denied!"));
                }
            }
            case Instructor -> {
                if (form.getOwner().getAssignee().getId() == user.getId()) {
                    createContent(form);
                } else {
                    add(new Span("access denied!"));
                }
            }
        }


    }
}
