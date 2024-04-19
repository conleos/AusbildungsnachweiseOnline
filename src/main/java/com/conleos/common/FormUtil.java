package com.conleos.common;

import com.conleos.data.entity.Form;
import com.vaadin.flow.component.html.Span;

public class FormUtil {
    private FormUtil() {
    }

    public static Span createFormBadge(Form form) {
        if (form == null || form.getStatus() == null) {
            return new Span("Create Form");
        }
        Span badge = new Span();
        switch (form.getStatus()) {
            case InProgress -> badge.getElement().setAttribute("theme", "badge");
            case InReview -> badge.getElement().setAttribute("theme", "badge contrast");
            case Signed -> badge.getElement().setAttribute("theme", "badge success");
            case Rejected -> badge.getElement().setAttribute("theme", "badge error");
        }

        switch (form.getStatus()) {
            case InProgress -> badge.setText("In Progress");
            case InReview -> badge.setText("In Review");
            case Signed -> badge.setText("Signed");
            case Rejected -> badge.setText("Rejected");
        }

        return badge;
    }

}
