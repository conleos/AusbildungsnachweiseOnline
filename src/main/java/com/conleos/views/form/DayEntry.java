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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Date;
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
    String totalTime = "";
    int totalMinutes;
    /*
     * FormEntry is optional
     * */
    public DayEntry(Day day, VerticalLayout container, Form.FormEntry entry, List<DayEntry> entries) {
        this.day = day;
        select = new Select<>();
        select.setLabel("Art");
        select.setItems(KindOfWork.values());
        select.setValue(KindOfWork.PracticalWork);
        select.addValueChangeListener(c -> {
           changeTotalTime();
           changePause();
        });
        timeBegin = new TimePicker("Von -");
        timeBegin.setStep(Duration.ofMinutes(15));
        timeEnd = new TimePicker("- Bis");
        timeEnd.setStep(Duration.ofMinutes(15));
        timeBegin.setValue(LocalTime.of(8, 0));
        timeEnd.setValue(LocalTime.of(16, 30));
        HorizontalLayout totalTimeLayout = new HorizontalLayout();
        timeSum = new TextField();
        timeSum.setReadOnly(true);
        timeSum.setLabel("Arbeitszeit:");
        Span hours = new Span("Stunden,Minuten");
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
            changePause();
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
        if (select.getValue() == KindOfWork.PracticalWork) {
            LocalTime timeB = timeBegin.getValue();
            LocalTime timeE = timeEnd.getValue();
            Duration totalTimeDuration = Duration.between(timeB, timeE);
            totalMinutes = (int) totalTimeDuration.toMinutes();
            int totalMinutesNoPause = totalMinutes - Integer.parseInt(pause.getValue());
            int hours = totalMinutesNoPause/60;
            int lastMinutes = totalMinutesNoPause;
            String zero = "";
            while (lastMinutes>60) {
                lastMinutes = lastMinutes-60;
            }
            if (lastMinutes==60) {
                lastMinutes=0;
                zero = "0";
            }
            totalTime = String.valueOf(hours);
            if (lastMinutes<10 && lastMinutes>0) {
                timeSum.setValue(totalTime + ":0" + lastMinutes + zero);
            }
            else {
                timeSum.setValue(totalTime + ":" + lastMinutes + zero);
            }
            changeTotalTimeColor();
        } else if (select.getValue() == KindOfWork.Schooling) {
            timeSum.setValue("7:58");
            changeTotalTimeColor();
        } else {
            timeSum.setValue("0");
            changeTotalTimeColor();
        }
    }

    void changeTotalTimeColor() {
        if (select.getValue() == KindOfWork.PracticalWork) {
            if (totalMinutes < 480 || totalMinutes - Double.parseDouble(pause.getValue())<480) {
                timeSum.removeClassName("background-green");
                timeSum.addClassName("background-red");
            } else {
                timeSum.removeClassName("background-red");
                timeSum.addClassName("background-green");
            }
        } else if (select.getValue() == KindOfWork.Schooling) {
            timeSum.removeClassName("background-green");
            timeSum.removeClassName("background-red");
        } else {
            timeSum.removeClassName("background-green");
            timeSum.removeClassName("background-red");
        }
    }

    void changePause () {
        if (select.getValue() == KindOfWork.PracticalWork) {
            pause.setReadOnly(false);
            try {
                Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
                LocalDate birthday = LocalDate.parse(session.getUser().getBirthday());
                LocalDate today = LocalDate.now();
                Period period = Period.between(birthday,today);
                boolean adult = period.getYears() >= 18;
                int old;
                if (adult) {
                    old = 30;
                } else {
                    old = 45;
                }
                int current = Integer.parseInt(pause.getValue());
                if (current < old) {
                    pause.removeClassName("background-green");
                    pause.addClassName("background-red");
                    changeTotalTime();
                } else {
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
        } else {
            pause.removeClassName("background-green");
            pause.removeClassName("background-red");
            pause.setValue("0");
            pause.setReadOnly(true);
        }
    }
}
