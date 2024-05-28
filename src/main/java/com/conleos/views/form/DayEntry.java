package com.conleos.views.form;

import com.conleos.common.FormUtil;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.i18n.Lang;
import com.conleos.views.admin.CreateErrorNotification;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;


public class DayEntry extends VerticalLayout {

    Select<KindOfWork> select;
    TimePicker timeBegin;
    TimePicker timeEnd;
    TextField timeSum;
    TextArea area;
    NumberField pause;
    CreateErrorNotificationForm noInt = new CreateErrorNotificationForm(Lang.translate("view.dayEntry.error.notification"));
    CreateErrorNotification error;
    Day day;
    FormView formView;
    Form.FormEntry formEntry;

    /*
     * FormEntry is optional
     * */
    public DayEntry(Day day, Form.FormEntry entry, FormView formView) {
        this.formView = formView;
        this.day = day;
        this.formEntry = entry;
        select = new Select<>();
        select.setLabel(Lang.translate("view.dayEntry.selectLabel.art"));
        select.setItems(KindOfWork.values());
        select.setValue(KindOfWork.PracticalWork);
        timeBegin = new TimePicker(Lang.translate("view.dayEntry.beginTime"));
        timeBegin.setStep(Duration.ofMinutes(15));
        timeEnd = new TimePicker(Lang.translate("view.dayEntry.endTime"));
        timeEnd.setStep(Duration.ofMinutes(15));
        timeBegin.setValue(LocalTime.of(0, 0));
        timeEnd.setValue(LocalTime.of(0, 0));
        HorizontalLayout totalTimeLayout = new HorizontalLayout();
        timeSum = new TextField();
        timeSum.setReadOnly(true);
        timeSum.setLabel(Lang.translate("view.dayEntry.sumTime"));
        totalTimeLayout.add(timeSum);
        HorizontalLayout pauseLayout = new HorizontalLayout();
        pause = new NumberField(Lang.translate("view.dayEntry.pause"));
        pause.setValue(0.0);
        pause.addClassName("background-red");
        pause.setWidth("150px");
        Span min = new Span("min");
        min.addClassNames("min");
        pauseLayout.add(pause, min);
        area = new TextArea(Lang.translate("view.dayEntry.area"));
        area.setWidthFull();

        VerticalLayout block = new VerticalLayout(select, timeBegin, timeEnd, pauseLayout);
        block.setClassName(LumoUtility.Padding.Left.NONE);
        block.setWidth("225px");

        HorizontalLayout h = new HorizontalLayout(block, area);
        h.setWidthFull();
        add(h, totalTimeLayout);


        if (entry != null) {
            if (entry.getKindOfWork() != null) {
                select.setValue(entry.getKindOfWork());
                area.setValue(entry.getDescription());
                timeBegin.setValue(entry.getBegin());
                timeEnd.setValue(entry.getEnd());
                pause.setValue((double) entry.getPause());
            }
        }

        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
        if (!session.getSessionRole().equals(Role.Trainee)) {
            select.setReadOnly(true);
            area.setReadOnly(true);
            timeBegin.setReadOnly(true);
            timeEnd.setReadOnly(true);

        }
        changeTotalTime();
        changePause(true);
        select.addValueChangeListener(c -> {
            changeTotalTime();
            changePause(false);
            formView.update();
        });
        timeBegin.addValueChangeListener(timeChange -> {
            changeTotalTime();
            formView.update();
        });
        timeEnd.addValueChangeListener(timeChange -> {
            changeTotalTime();
            formView.update();
        });
        pause.addValueChangeListener(p -> {
            changePause(false);
            formView.update();
        });
    }

    public Form.FormEntry createFormEntry(Form form) {
        Form.FormEntry entry = new Form.FormEntry();

        entry.setKindOfWork(select.getValue());
        entry.setDescription(area.getValue());
        entry.setBegin(timeBegin.getValue());
        entry.setEnd(timeEnd.getValue());
        entry.setPause(pause.getValue().intValue());
        return entry;
    }

    void changeTotalTime() {
        formEntry.setBegin(timeBegin.getValue());
        formEntry.setEnd(timeEnd.getValue());
        formEntry.setPause(pause.getValue().intValue());
        formEntry.setKindOfWork(select.getValue());
        int totalMinutesNoPause = FormUtil.getTotalMinutesFromEntry(formEntry);

        timeSum.setValue(FormUtil.getLabelFromTotalTime(totalMinutesNoPause));
    }

    void changePause(Boolean constructor) {
        if (select.getValue() == KindOfWork.PracticalWork) {
            pause.setReadOnly(false);
            try {
                Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
                LocalDate birthday = session.getUser().getBirthday();
                LocalDate today = LocalDate.now();
                Period period = Period.between(birthday, today);
                final boolean adult = period.getYears() >= 18;
                int old = adult ? 30 : 45;
                int current = pause.getValue().intValue();
                if (current < old) {
                    pause.removeClassName("background-green");
                    pause.addClassName("background-red");
                    if (!constructor) {
                        error = new CreateErrorNotification(Lang.translate("view.dayEntry.pause.error1") + " "+ old + " " + Lang.translate("view.dayEntry.pause.error2"));
                        error.open();
                    }
                    changeTotalTime();
                } else {
                    pause.removeClassName("background-red");
                    pause.addClassName("background-green");
                    changeTotalTime();
                }
            } catch (RuntimeException e) {
                System.out.println(e.toString());
                noInt.open();
            }
        } else {
            pause.removeClassName("background-green");
            pause.removeClassName("background-red");
            pause.setValue(0.0);
            pause.setReadOnly(true);
        }
    }
}
