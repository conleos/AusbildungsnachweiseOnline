package com.conleos.views.form;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalTime;
import java.util.List;

public class DayEntry extends HorizontalLayout {

    Select<KindOfWork> select;
    TimePicker timeBegin;
    TimePicker timeEnd;
    TextArea area;

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
        timeEnd.setValue(LocalTime.of(16,0));
        timeEnd.addValueChangeListener(timeChange -> {

        });
        area = new TextArea("Beschreibung");
        area.setWidthFull();
        Button delBtn = new Button(VaadinIcon.CLOSE.create());
        delBtn.addClickListener(eventDel -> {
            Component parent = delBtn.getParent().get();
            if (parent instanceof HorizontalLayout) {
                container.remove(parent);
                entries.remove(parent);
            }
        });
        VerticalLayout block = new VerticalLayout(select, timeBegin, timeEnd);
        block.setWidth("225px");

        add(block, area, delBtn);
        setWidthFull();

        if (entry != null) {
            select.setValue(entry.getKindOfWork());
            area.setValue(entry.getDescription());
            timeBegin.setValue(entry.getBegin());
            timeEnd.setValue(entry.getEnd());
        }

        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
        if (!session.getSessionRole().equals(Role.Trainee)) {
            select.setReadOnly(true);
            area.setReadOnly(true);
            timeBegin.setReadOnly(true);
            timeEnd.setReadOnly(true);
            delBtn.setEnabled(false);
        }
    }

    public Form.FormEntry createFormEntry(Form form) {
        Form.FormEntry entry = new Form.FormEntry();

        entry.setDate(day.date);
        entry.setKindOfWork(select.getValue());
        entry.setDescription(area.getValue());
        entry.setBegin(timeBegin.getValue());
        entry.setEnd(timeEnd.getValue());

        return entry;
    }
}
