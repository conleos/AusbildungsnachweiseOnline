package com.conleos.views.form;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class DayEntry extends VerticalLayout {

    Select<KindOfWork> select;
    TimePicker timeBegin;
    TimePicker timeEnd;
    TextField timeSum;
    TextArea area;
    TextField pause;
    Boolean errorBreak = false;
    Day day;
    Double totalTime = 0.0;
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
        timeBegin.setStep(Duration.ofMinutes(15));
        timeEnd = new TimePicker("- Bis");
        timeEnd.setStep(Duration.ofMinutes(15));
        timeBegin.setValue(LocalTime.of(8, 0));
        timeEnd.setValue(LocalTime.of(16, 30));
        HorizontalLayout totalTimeLayout = new HorizontalLayout();
        timeSum = new TextField();
        timeSum.setReadOnly(true);
        timeSum.setLabel("Zeit gesamt:");
        Span hours = new Span("h");
        hours.addClassNames("min");
        totalTimeLayout.add(timeSum,hours);
        timeBegin.addValueChangeListener(timeChange -> {
            changeTotalTime();
        });
        timeEnd.addValueChangeListener(timeChange -> {
            changeTotalTime();
        });
        HorizontalLayout pauseLayout = new HorizontalLayout();
        pause = new TextField("Davon Pause");
        pause.setValue("0");
        pause.addClassName("background-red");
        pause.setWidth("100px");
        pause.addValueChangeListener(p -> {
            try {
                int current = Integer.parseInt(p.getValue());
                if (current<30) {
                    pause.removeClassName("background-green");
                    pause.addClassName("background-red");
                    changeTotalTimeColor();
                }
                else {
                    pause.removeClassName("background-red");
                    pause.addClassName("background-green");
                    changeTotalTime();
                }
            } catch (RuntimeException e) {
                System.out.println(e.toString());
                if (!errorBreak) {
                    CreateErrorNotificationForm noInt = new CreateErrorNotificationForm("Bitte gib einen gültigen Zahlenwert für die Pause ein.");
                    noInt.open();
                    errorBreak = true;
                }
            }


        });
        Span min = new Span("min");
        min.addClassNames("min");
        pauseLayout.add(pause,min);
        area = new TextArea("Beschreibung");
        area.setWidthFull();

        VerticalLayout block = new VerticalLayout(select, timeBegin, timeEnd, pauseLayout);
        block.setClassName(LumoUtility.Padding.Left.NONE);
        block.setWidth("225px");

        HorizontalLayout h = new HorizontalLayout(block, area);
        h.setWidthFull();
        changeTotalTime();
        add(h, totalTimeLayout);


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
        entry.setTotalTime(totalTime);
        return entry;
    }

    void changeTotalTime() {
        LocalTime timeB = timeBegin.getValue();
        LocalTime timeE = timeEnd.getValue();
        Duration totalTimeDuration = Duration.between(timeB,timeE);
        totalTime = (double) totalTimeDuration.toMinutes()/60;
        BigDecimal round = BigDecimal.valueOf(totalTime).setScale(2, RoundingMode.HALF_UP);
        totalTime = round.doubleValue();
        timeSum.setValue(String.valueOf(totalTime));
        changeTotalTimeColor();
    }

    void changeTotalTimeColor() {
        if (totalTime<8.5 || totalTime - Double.parseDouble(pause.getValue())/60 < 8) {
            timeSum.removeClassName("background-green");
            timeSum.addClassName("background-red");
        } else {
            timeSum.removeClassName("background-red");
            timeSum.addClassName("background-green");
        }
    }
}
