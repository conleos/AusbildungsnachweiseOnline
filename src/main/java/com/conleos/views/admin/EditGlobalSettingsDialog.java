package com.conleos.views.admin;

import com.conleos.common.Role;
import com.conleos.data.entity.GlobalSettings;
import com.conleos.data.entity.User;
import com.conleos.data.service.GlobalSettingsService;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.List;
import java.util.Locale;

public class EditGlobalSettingsDialog extends Dialog {

    private Locale locale = UI.getCurrent().getLocale();

    public EditGlobalSettingsDialog() {
        GlobalSettings globalSettings = GlobalSettingsService.getGlobalSettings();
        setHeaderTitle(getTranslation("view.editSettings.headerTitle", locale));

        List<User> assignees = UserService.getInstance().getAllUsers();
        assignees.removeIf(user -> user.getRole() == Role.Trainee);

        setWidth("45%");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setWidthFull();
        add(dialogLayout);

        NumberField hoursField = new NumberField("Hours In a Week");
        hoursField.setMin(1.0);
        hoursField.setValue(globalSettings.getHoursInAWeek());
        dialogLayout.add(hoursField);

        Button saveButton = new Button(getTranslation("view.createUser.button.save", locale), e -> {
            globalSettings.setHoursInAWeek(hoursField.getValue());
            GlobalSettingsService.save(globalSettings);
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button(getTranslation("view.createUser.button.cancel", locale), e -> close());
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

}
