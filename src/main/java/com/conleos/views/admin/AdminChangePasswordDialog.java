package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.data.service.UserService;
import com.conleos.i18n.Lang;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;


public class AdminChangePasswordDialog extends Dialog {

    Long gridUserId;

    public AdminChangePasswordDialog(Long userID) {
        this.gridUserId = userID;

        H2 changeHeader = new H2(Lang.translate("view.adminChangePw.changePassword"));
        Text text = new Text(Lang.translate("view.adminChangePw.textSure"));
        PasswordField newPasswordField = new PasswordField(Lang.translate("view.adminChangePw.newPassword"));
        VerticalLayout layout = new VerticalLayout();
        layout.add(changeHeader, text, newPasswordField);
        add(layout);
        Button changeButton = new Button(Lang.translate("view.adminChangePw.button.change"));
        changeButton.addClickListener(clickEvent -> {
            UserService.getInstance().setNewPassword(PasswordHasher.hash(newPasswordField.getValue()), gridUserId);
            close();
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        changeButton.getStyle().set("margin-right", "auto");
        getFooter().add(changeButton);
        Button cancelButton = new Button(Lang.translate("view.adminChangePw.button.cancel"), (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(cancelButton);
        Button change = new Button(Lang.translate("view.adminChangePw.button.changePw"), (e) -> open());
    }
}
