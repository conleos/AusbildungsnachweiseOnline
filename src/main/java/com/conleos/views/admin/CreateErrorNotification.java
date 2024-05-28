package com.conleos.views.admin;

import com.conleos.i18n.Lang;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

//Pass any string for error message.

public class CreateErrorNotification extends Notification {

    public CreateErrorNotification(String s) {
        addThemeVariants(NotificationVariant.LUMO_ERROR);
        Div text = new Div(new Text(s));
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel(Lang.translate("view.admin.errorNotification"));
        closeButton.addClickListener(e -> {
            close();
        });
        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(layout);
    }
}
