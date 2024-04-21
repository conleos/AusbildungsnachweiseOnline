package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.core.Session;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.Text;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;

public class CreateAdminChangePwDialog extends Dialog {

    Long gridUserId;
    public CreateAdminChangePwDialog(Long i) {

        this.gridUserId = i;

        H2 changeHeader = new H2("Change Password");
        Text sure = new Text("Are you sure you want to change user password?");
        PasswordField newPasswordField = new PasswordField("New Password");
        VerticalLayout input = new VerticalLayout();
        input.add(changeHeader,sure,newPasswordField);
        add(input);
        Button changeButton = new Button("Change");
        changeButton.addClickListener(clickEvent -> {
            UserService.getInstance().setNewPassword(PasswordHasher.hash(newPasswordField.getValue()),gridUserId);
            close();
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        changeButton.getStyle().set("margin-right", "auto");
        getFooter().add(changeButton);
        Button cancelButton = new Button("Cancel", (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(cancelButton);
        Button change = new Button("Change Password", (e) -> open());
    }
}
