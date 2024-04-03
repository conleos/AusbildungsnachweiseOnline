package com.conleos.views.form;

import com.conleos.data.entity.Form;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

public class Day {
    LocalDate date;
    List<DayEntry> entries = new ArrayList<>();

    public Day (int i) {
        LocalDate beginOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        this.date = beginOfWeek.plusDays(i);
    }

    public VerticalLayout createFormContentForDay(Form form,int i) {
        String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, UI.getCurrent().getLocale());

        VerticalLayout container = new VerticalLayout();
        container.setWidthFull();

        Button addBtn = new Button("Add", VaadinIcon.PLUS.create());
        VerticalLayout day = new VerticalLayout();
        day.setClassName(Border.ALL);
        TextField timeSum = new TextField();
        timeSum.setLabel("Zeit gesamt:");

        addBtn.addClickListener(event -> {
            DayEntry dayEntry = new DayEntry(this, container, null);
            entries.add(dayEntry);
            container.add(dayEntry);
        });

        // Init the Container with Content from Database
        List<Form.FormEntry> initEntries = form.getEntriesByDate(date);
        for (Form.FormEntry entry : initEntries) {
            DayEntry dayEntry = new DayEntry(this, container, entry);
            entries.add(dayEntry);
            container.add(dayEntry);
        }

        day.add(new Span(dayLabel), container, addBtn, timeSum);
        day.addClassName("day");
        if(i%2==0) {
            day.addClassNames("grey-background");
        }
        return day;
    }

    public List<Form.FormEntry> getEntries(Form form) {
        List<Form.FormEntry> result = new ArrayList<>();

        for (DayEntry It : entries) {
            result.add(It.createFormEntry(form));
        }

        return result;
    }
}
