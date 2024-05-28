package com.conleos.views.form;

import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.i18n.Lang;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;

import java.time.DayOfWeek;
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
        setValue(null);
        setLabel(Lang.translate("view.dateBasedNavigator.label"));
        addValueChangeListener(event -> {
            navigate(trainee, event.getValue());
        });
    }

    private void navigate(User trainee, LocalDate date) {
        LocalDate beginOfWeek = date.with(DayOfWeek.MONDAY);

        Form form = FormService.getInstance().getFormByDateAndUser(beginOfWeek, trainee);
        if (form == null) {
            Notification.show(Lang.translate("view.dateBasedNavigator.notification"), 4000, Notification.Position.BOTTOM_START);
        } else {
            // We need to trigger a Page Reload
            UI.getCurrent().access(() -> UI.getCurrent().navigate("/"));
            UI.getCurrent().access(() -> UI.getCurrent().navigate("/form/" + form.getId()));
        }

    }

}
