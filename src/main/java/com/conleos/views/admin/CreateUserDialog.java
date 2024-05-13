package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.data.validation.UserDataValidation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateUserDialog extends Dialog {

    private Locale locale = UI.getCurrent().getLocale();

    public CreateUserDialog() {
        setHeaderTitle(getTranslation("view.createUser.headerTitle", locale));

        List<User> assignees = UserService.getInstance().getAllUsers();
        assignees.removeIf(user -> user.getRole() == Role.Trainee);

        setWidth("45%");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setWidthFull();
        add(dialogLayout);

        TextField username = new TextField(getTranslation("view.createUser.label.username", locale));
        username.setWidthFull();
        PasswordField password = new PasswordField(getTranslation("view.createUser.label.password", locale));
        password.setWidthFull();
        TextField firstName = new TextField(getTranslation("view.createUser.label.firstname", locale));
        firstName.setWidthFull();
        TextField lastName = new TextField(getTranslation("view.createUser.label.lastname", locale));
        lastName.setWidthFull();
        DatePicker birthday = new DatePicker(getTranslation("view.createUser.label.birthday", locale));
        birthday.setWidthFull();
        TextField email = new TextField(getTranslation("view.createUser.label.email", locale));
        email.setWidthFull();

        MultiSelectComboBox<User> assigneeSelect = new MultiSelectComboBox<>(getTranslation("view.createUser.MultiSelectComboBox.label.assignedTo", locale));
        assigneeSelect.setPlaceholder(getTranslation("view.createUser.MultiSelectComboBox.label.setPlace", locale));
        assigneeSelect.setItems(assignees);
        assigneeSelect.setItemLabelGenerator(User::getUsername);
        assigneeSelect.setEnabled(false);

        DatePicker startTimeSelector = new DatePicker(getTranslation("view.createUser.timeSelector.label", locale));
        startTimeSelector.setValue(LocalDate.now());
        startTimeSelector.setEnabled(false);

        ComboBox<Role> roleSelect = new ComboBox<>(getTranslation("view.createUser.comboBox.label.roleSelect", locale));
        roleSelect.setPlaceholder(getTranslation("view.createUser.comboBox.label.setPlace", locale));
        roleSelect.setItems(Role.values());
        roleSelect.setItemLabelGenerator(Role::toString);
        roleSelect.addValueChangeListener(event -> {
            assigneeSelect.setEnabled(roleSelect.getValue().equals(Role.Trainee));
            startTimeSelector.setEnabled(roleSelect.getValue().equals(Role.Trainee));
        });

        dialogLayout.add(username, password, firstName, lastName, birthday, email, roleSelect, assigneeSelect, startTimeSelector);

        Button saveButton = new Button(getTranslation("view.createUser.button.save", locale), e -> {
            User user = new User(username.getValue(), PasswordHasher.hash(password.getValue()), roleSelect.getValue(), firstName.getValue(), lastName.getValue(), birthday.getValue());
            user.setEmail(email.getValue());
            user.setAssignees(new ArrayList<>(assigneeSelect.getValue()));
            user.setStartDate(startTimeSelector.getValue());

            UserDataValidation.Result result = UserDataValidation.validateNewUserData(user);
            if (result.isValid) {
                UserService.getInstance().saveUser(user);
                close();
                UI.getCurrent().getPage().reload();
            } else {
                Notification.show(result.errorMsg, 4000, Notification.Position.BOTTOM_START);
            }

        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button(getTranslation("view.createUser.button.cancel", locale), e -> close());
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

}
