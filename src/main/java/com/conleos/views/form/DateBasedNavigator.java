package com.conleos.views.form;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.time.LocalDate;

/*
* This DatePicker is used to navigate to specific Form by picking a date.
* An EventListener will automatically reroute the client.
* */
public class DateBasedNavigator extends DatePicker {

    public DateBasedNavigator(LocalDate date) {
        setValue(date);
        setLabel("Navigate by Date");
        addValueChangeListener(event -> {
            // TODO: Missing Implementation
        });
    }

}
