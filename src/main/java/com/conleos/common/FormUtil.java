package com.conleos.common;

import com.conleos.data.entity.Form;
import com.conleos.views.form.KindOfWork;
import com.vaadin.flow.component.html.Span;

import java.awt.*;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class FormUtil {
    private FormUtil() {
    }

    public static Span createFormBadge(Form form) {
        if (form == null || form.getStatus() == null) {
            return new Span("Create Form");
        }
        Span badge = new Span();
        switch (form.getStatus()) {
            case InProgress, InReview -> badge.getElement().setAttribute("theme", "badge");
            case Signed -> badge.getElement().setAttribute("theme", "badge success");
            case Rejected -> badge.getElement().setAttribute("theme", "badge error");
        }

        switch (form.getStatus()) {
            case InProgress -> badge.setText("In Progress");
            case InReview -> badge.setText("In Review");
            case Signed -> badge.setText("Signed");
            case Rejected -> badge.setText("Rejected");
        }
        badge.getStyle().set("background-color",  HtmlColor.from(ColorGenerator.statusColor(form.getStatus())).toString());
        badge.addClassNames("text-white");
        return badge;
    }

    public static int getTotalMinutesFromEntry(LocalTime begin, LocalTime end, int pauseInMinutes, KindOfWork kindOfWork) {
        if (kindOfWork.equals(KindOfWork.PracticalWork)) {
            return (int) Math.max(MINUTES.between(begin, end) - pauseInMinutes, 0);
        } else if (kindOfWork.equals(KindOfWork.Schooling)) {
            return 7 * 60 + 58;
        } else {
            return 0;
        }
    }

    public static String getLabelFromTotalTime(int timeInMinutes) {
        return String.format("%d:%02d", timeInMinutes / 60, timeInMinutes % 60);
    }

}
