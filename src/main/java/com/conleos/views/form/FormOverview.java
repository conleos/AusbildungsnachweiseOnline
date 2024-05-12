package com.conleos.views.form;

import com.conleos.data.entity.Form;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;

public class FormOverview extends Tab {

    public static VerticalLayout createContent(Form form) {
        VerticalLayout layout = new VerticalLayout();

        layout.add(addEntry(form.getMonday(), "Montag", true));
        layout.add(addEntry(form.getTuesday(), "Dienstag", true));
        layout.add(addEntry(form.getWednesday(), "Mittwoch", true));
        layout.add(addEntry(form.getThursday(), "Donnerstag", true));
        layout.add(addEntry(form.getFriday(), "Freitag", true));
        layout.add(addEntry(form.getSaturday(), "Samstag", false));
        layout.add(addEntry(form.getSunday(), "Sonntag", false));

        return layout;
    }

    private static Component addEntry(Form.FormEntry entry, String dayLabel, boolean opened) {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span(entry.getKindOfWork().toString()));
        layout.add(new Span(String.format("Von: %s Bis: %s mit einer Pausenzeit von %s Minuten.", entry.getBegin(), entry.getEnd(), entry.getPause())));

        TextArea text = new TextArea("Beschreibung");
        text.setReadOnly(true);
        text.setWidthFull();
        text.setValue(entry.getDescription());
        layout.add(text);

        Details details = new Details(dayLabel, layout);
        details.setOpened(opened);
        details.setWidthFull();

        return details;
    }

}
