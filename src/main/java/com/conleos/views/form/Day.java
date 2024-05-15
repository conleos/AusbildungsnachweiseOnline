package com.conleos.views.form;

import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.time.LocalDate;
import java.time.format.TextStyle;

public class Day {
    LocalDate date;
    DayEntry entry;
    VerticalLayout container = new VerticalLayout();
    FormView formView;

    public Day(LocalDate date, FormView formView) {
        this.date = date;
        this.formView = formView;
    }

    public VerticalLayout createFormContentForDay(Form form, int dayOffset) {
        String dayLabel = getLocalDayName();
        container.setWidthFull();

        VerticalLayout day = new VerticalLayout();
        day.setClassName(Border.ALL);

        // Init the Container with Content from Database
        Form.FormEntry entry = form.getEntryByDate(date);
        if (entry != null) {
            this.entry = new DayEntry(this, entry, formView);
            container.add(this.entry);
        } else {
            this.entry = new DayEntry(this, null, formView);
            container.add(this.entry);
        }
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        day.add(new Span(dayLabel), container);
        day.addClassName("day");
        return day;
    }

    public Form.FormEntry getEntry(Form form) {
        return entry.createFormEntry(form);
    }

    public String getLocalDayName() {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, UI.getCurrent().getLocale());
    }

    public LocalDate getDate() {
        return date;
    }
}
