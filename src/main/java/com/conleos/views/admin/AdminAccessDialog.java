package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.core.Session;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.server.VaadinSession;

import java.util.Locale;

public class AdminAccessDialog extends Dialog {

    Long gridUserId;
    private Locale locale = UI.getCurrent().getLocale();

    public AdminAccessDialog(Long userID, Dialog dialogToBeOpened) {
        this.gridUserId = userID;
        H2 changeHeader = new H2(getTranslation("view.adminAccess.label.adminAccess", locale));
        Text sure = new Text(getTranslation("view.adminAccess.label.textSure", locale));
        PasswordField adminPasswordField = new PasswordField(getTranslation("view.adminAccess.label.password", locale));
        VerticalLayout input = new VerticalLayout();
        input.add(changeHeader, sure, adminPasswordField);
        add(input);
        Button submitButton = new Button(getTranslation("view.adminAccess.button.submit", locale));
        submitButton.addClickListener(clickEvent -> {
            if (PasswordHasher.hash(adminPasswordField.getValue()).equals(Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser().getPasswordHash())) {
                dialogToBeOpened.open();
                close();
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        submitButton.getStyle().set("margin-right", "auto");
        getFooter().add(submitButton);
        Button cancelButton = new Button(getTranslation("view.adminAccess.button.cancel", locale), (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(cancelButton);
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        Div text = new Div(new Text(getTranslation("view.adminAccess.div.label", locale)));
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel(getTranslation("view.adminAccess.button.close", locale));
        closeButton.addClickListener(e -> {
            notification.close();
        });
        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        notification.add(layout);

    }
}
