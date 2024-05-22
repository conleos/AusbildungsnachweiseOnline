package com.conleos.views.dashboard;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.Locale;

public class RequestNotification extends Notification {

        private Locale locale = UI.getCurrent().getLocale();

        public RequestNotification(String s) {
            addThemeVariants(NotificationVariant.LUMO_ERROR);
            Div text = new Div(new Text(s));
            Button closeButton = new Button(new Icon("lumo", "cross"));
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.setAriaLabel(getTranslation("view.admin.errorNotification", locale));
            closeButton.addClickListener(e -> {
                close();
            });
            HorizontalLayout layout = new HorizontalLayout(text, closeButton);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            add(layout);
        }
    }
