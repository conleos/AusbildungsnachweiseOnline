package com.conleos.views.profile;

import com.conleos.common.PasswordHasher;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.awt.*;

@PageTitle("Edit your Profile")
@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout {

    public ProfileView(UserService service) {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
        add(createContent(session.getUser()));

    }

    private static Component createContent(User user) {
        TextField firstName = new TextField("First Name");
        firstName.setValue(user.getFirstName());
        firstName.setEnabled(false);
        TextField lastName = new TextField("Last Name");
        lastName.setValue(user.getLastName());
        lastName.setEnabled(false);
        Dialog dialog = new Dialog();
        H2 changeHeader = new H2("Change Password");
        Text sure = new Text("Are you sure you want to change your password?");
        PasswordField oldPasswordField = new PasswordField("Old Password");
        PasswordField newPasswordField = new PasswordField("New Password");
        Text wrong = new Text("The old password is not correct!");
        VerticalLayout input = new VerticalLayout();
        input.add(changeHeader,sure,oldPasswordField,newPasswordField);
        dialog.add(input);
        Button changeButton = new Button("Change");
        changeButton.addClickListener(clickEvent -> {
            if (PasswordHasher.hash(oldPasswordField.getValue()).equals(user.getPasswordHash())) {
                System.out.println(PasswordHasher.hash(newPasswordField.getValue()));
                UserService.getInstance().setNewPassword(PasswordHasher.hash(newPasswordField.getValue()), user.getId());
                dialog.close();
           } else {
                input.add(wrong);
            }
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        changeButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(changeButton);
        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        Button change = new Button("Change Password", e -> dialog.open());
        return new VerticalLayout(firstName, lastName, dialog, change);
    }

}