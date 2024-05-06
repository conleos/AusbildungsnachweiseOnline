package com.conleos.views.form;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalTime;
import java.util.List;

public class DayEntry extends HorizontalLayout {

    Select<KindOfWork> select;
    TimePicker timeBegin;
    TimePicker timeEnd;
    TextArea area;
    TextField pause;

    Day day;

    /*
     * FormEntry is optional
     * */
    public DayEntry(Day day, VerticalLayout container, Form.FormEntry entry, List<DayEntry> entries) {
        this.day = day;
        select = new Select<>();
        select.setLabel("Art");
        select.setItems(KindOfWork.values());
        select.setValue(KindOfWork.PracticalWork);

        timeBegin = new TimePicker("Von -");
        timeBegin.setValue(LocalTime.of(8, 0));
        timeBegin.addValueChangeListener(timeChange -> {

        });
        timeEnd = new TimePicker("- Bis");
        timeEnd.setValue(LocalTime.of(16, 0));
        timeEnd.addValueChangeListener(timeChange -> {

        });
        HorizontalLayout pauseLayout = new HorizontalLayout();
        pause = new TextField("Davon Pause");
        pause.setWidth("100px");
        pause.addValueChangeListener(p -> {
            int current = Integer.parseInt(p.getValue());
            if (current<30) {
                pause.removeClassName("background-green");
                pause.addClassName("background-red");
            }
            else {
                pause.removeClassName("background-red");
                pause.addClassName("background-green");
            }
        });
        Span min = new Span("min");
        min.addClassNames("min");
        pauseLayout.add(pause,min);
        area = new TextArea("Beschreibung");
        area.setWidthFull();

        VerticalLayout block = new VerticalLayout(select, timeBegin, timeEnd, pauseLayout);
        block.setWidth("225px");

        add(block, area);
        setWidthFull();

        if (entry != null) {
            select.setValue(entry.getKindOfWork());
            area.setValue(entry.getDescription());
            timeBegin.setValue(entry.getBegin());
            timeEnd.setValue(entry.getEnd());
            pause.setValue(entry.getPause());
        }

        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
        if (!session.getSessionRole().equals(Role.Trainee)) {
            select.setReadOnly(true);
            area.setReadOnly(true);
            timeBegin.setReadOnly(true);
            timeEnd.setReadOnly(true);

        }
    }

    public Form.FormEntry createFormEntry(Form form) {
        Form.FormEntry entry = new Form.FormEntry();

        entry.setDate(day.date);
        entry.setKindOfWork(select.getValue());
        entry.setDescription(area.getValue());
        entry.setBegin(timeBegin.getValue());
        entry.setEnd(timeEnd.getValue());
        entry.setPause(pause.getValue());
        return entry;
    }
}
