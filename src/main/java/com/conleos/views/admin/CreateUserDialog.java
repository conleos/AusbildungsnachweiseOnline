package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class CreateUserDialog extends Dialog {

    public CreateUserDialog() {
        setHeaderTitle("Add a new User");

        setWidth("45%");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setWidth("45%");
        add(dialogLayout);

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Initial Password");
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        TextField email = new TextField("Email");

        dialogLayout.add(username, password, firstName, lastName, email);

        Button saveButton = new Button("Create", e -> {
            User user = new User(username.getValue(), PasswordHasher.hash(password.getValue()), Role.Admin, firstName.getValue(), lastName.getValue());
            user.setEmail(email.getValue());
            UserService.getInstance().createUser(user);
            close();
            UI.getCurrent().getPage().reload();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> close());
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

}
