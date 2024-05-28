package com.conleos.views.form;

import com.conleos.data.entity.Form;
import com.conleos.i18n.Lang;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;

public class FormOverview extends Tab {

    public static VerticalLayout createContent(Form form) {
        VerticalLayout layout = new VerticalLayout();

        layout.add(addEntry(form.getMonday(), Lang.translate("view.form.overview.monday"), true));
        layout.add(addEntry(form.getTuesday(), Lang.translate("view.form.overview.tuesday"), true));
        layout.add(addEntry(form.getWednesday(), Lang.translate("view.form.overview.wednesday"), true));
        layout.add(addEntry(form.getThursday(), Lang.translate("view.form.overview.thursday"), true));
        layout.add(addEntry(form.getFriday(), Lang.translate("view.form.overview.friday"), true));
        layout.add(addEntry(form.getSaturday(), Lang.translate("view.form.overview.saturday"), false));
        layout.add(addEntry(form.getSunday(), Lang.translate("view.form.overview.sunday"), false));

        return layout;
    }

    private static Component addEntry(Form.FormEntry entry, String dayLabel, boolean opened) {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span(entry.getKindOfWork().toString()));
        layout.add(new Span(String.format(Lang.translate("view.form.overview.layout1") + " %s " + Lang.translate("view.form.overview.layout2") + " %s " + Lang.translate("view.form.overview.layout3") + " %s " + Lang.translate("view.form.overview.layout4"), entry.getBegin(), entry.getEnd(), entry.getPause())));

        TextArea text = new TextArea(Lang.translate("view.form.overview.text"));
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
