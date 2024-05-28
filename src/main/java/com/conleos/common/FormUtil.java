package com.conleos.common;

import com.conleos.data.entity.Form;
import com.conleos.i18n.Lang;
import com.conleos.views.form.KindOfWork;
import com.vaadin.flow.component.html.Span;


import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class FormUtil {
    private FormUtil() {
    }

    public static Span createFormBadge(Form form) {
        if (form == null || form.getStatus() == null) {
            return new Span(Lang.translate("view.span"));
        }
        Span badge = new Span();
        switch (form.getStatus()) {
            case InProgress, InReview -> badge.getElement().setAttribute("theme", "badge");
            case Signed -> badge.getElement().setAttribute("theme", "badge success");
            case Rejected -> badge.getElement().setAttribute("theme", "badge error");
        }
        switch (form.getStatus()) {
            case InProgress -> badge.setText(Lang.translate("view.formUtil1"));
            case InReview -> badge.setText(Lang.translate("view.formUtil2"));
            case Signed -> badge.setText(Lang.translate("view.formUtil3"));
            case Rejected -> badge.setText(Lang.translate("view.formUtil4"));
        }
        badge.getStyle().set("background-color",  HtmlColor.from(ColorGenerator.statusColor(form.getStatus())).toString());
        badge.addClassNames("text-white");
        return badge;
    }

    public static int getTotalMinutesFromEntry(LocalTime begin, LocalTime end, int pauseInMinutes, KindOfWork kindOfWork) {
        if (kindOfWork.equals(KindOfWork.PracticalWork)) {
            return (int) Math.max(MINUTES.between(begin, end) - pauseInMinutes, 0);
        } else if (kindOfWork.equals(KindOfWork.Schooling)) {
            return 7 * 60 + 48;
        } else {
            return 0;
        }
    }
    public static int getTotalMinutesFromEntry(Form.FormEntry entry) {
        return getTotalMinutesFromEntry(entry.getBegin(), entry.getEnd(), entry.getPause(), entry.getKindOfWork());
    }

    public static String getLabelFromTotalTime(int timeInMinutes) {
        return String.format("%d:%02d", timeInMinutes / 60, timeInMinutes % 60);
    }
    public static String getLabelFromTotalTimeOfForm(Form form) {
        int minutes = 0;

        minutes += FormUtil.getTotalMinutesFromEntry(form.getMonday());
        minutes += FormUtil.getTotalMinutesFromEntry(form.getTuesday());
        minutes += FormUtil.getTotalMinutesFromEntry(form.getWednesday());
        minutes += FormUtil.getTotalMinutesFromEntry(form.getThursday());
        minutes += FormUtil.getTotalMinutesFromEntry(form.getFriday());
        minutes += FormUtil.getTotalMinutesFromEntry(form.getSaturday());
        minutes += FormUtil.getTotalMinutesFromEntry(form.getSunday());

        return getLabelFromTotalTime(minutes);
    }

}
