package com.conleos.views.form;

import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.vaadin.flow.component.datepicker.DatePicker;

import java.time.LocalDate;

/*
 * This DatePicker is used to navigate to specific Form by picking a date.
 * An EventListener will automatically reroute the client.
 * */
public class DateBasedNavigator extends DatePicker {


    public DateBasedNavigator(Form form) {
        if (form != null) {
            init(form.getOwner(), form.getMondayDate());
        }
    }

    public DateBasedNavigator(User trainee, LocalDate date) {
        init(trainee, date);
    }

    private void init(User trainee, LocalDate date) {
        setValue(date);
        setLabel("Navigate by Date");
        addValueChangeListener(event -> {
            // TODO: Missing Implementation
        });
    }

}
