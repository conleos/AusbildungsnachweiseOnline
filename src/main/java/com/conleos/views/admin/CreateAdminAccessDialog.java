package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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

import java.time.LocalDate;
import java.util.List;

public class CreateAdminAccessDialog extends Dialog {

    Long gridUserId;
    private final CreateAdminChangePwDialog changePwDialog;
    public CreateAdminAccessDialog(Long i) {
        this.gridUserId = i;
        changePwDialog = new CreateAdminChangePwDialog(gridUserId);
        H2 changeHeader = new H2("Admin Access");
        Text sure = new Text("Please enter your Admin Password");
        TextField adminPasswordField = new TextField("Password");
        VerticalLayout input = new VerticalLayout();
        input.add(changeHeader,sure,adminPasswordField);
        add(input);
        Button submitButton = new Button("Submit");
        submitButton.addClickListener(clickEvent -> {
            if (PasswordHasher.hash(adminPasswordField.getValue()).equals(Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser().getPasswordHash())) {
                changePwDialog.open();
                close();
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        submitButton.getStyle().set("margin-right", "auto");
        getFooter().add(submitButton);
        Button cancelButton = new Button("Cancel", (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(cancelButton);
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        Div text = new Div(new Text("No user chosen!"));
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(e -> {
            notification.close();
        });
        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        notification.add(layout);

    }
}
