package com.conleos.views.admin;

import com.conleos.common.Role;
import com.conleos.data.entity.GlobalSettings;
import com.conleos.data.entity.User;
import com.conleos.data.service.GlobalSettingsService;
import com.conleos.data.service.UserService;
import com.conleos.i18n.Lang;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.List;


public class EditGlobalSettingsDialog extends Dialog {

    public EditGlobalSettingsDialog() {
        GlobalSettings globalSettings = GlobalSettingsService.getGlobalSettings();
        setHeaderTitle(Lang.translate("view.editSettings.headerTitle"));

        List<User> assignees = UserService.getInstance().getAllUsers();
        assignees.removeIf(user -> user.getRole() == Role.Trainee);

        setWidth("45%");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setWidthFull();
        add(dialogLayout);

        NumberField hoursField = new NumberField(Lang.translate("view.editSettings.hoursField"));
        hoursField.setMin(1.0);
        hoursField.setValue(globalSettings.getHoursInAWeek());
        dialogLayout.add(hoursField);

        Button saveButton = new Button(Lang.translate("view.createUser.button.save"), e -> {
            globalSettings.setHoursInAWeek(hoursField.getValue());
            GlobalSettingsService.save(globalSettings);
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button(Lang.translate("view.createUser.button.cancel"), e -> close());
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

}
