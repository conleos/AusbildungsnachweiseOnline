package com.conleos.views.admin;

import com.conleos.common.PasswordHasher;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

import java.util.Locale;

public class AdminChangePasswordDialog extends Dialog {

    Long gridUserId;
    private Locale locale = UI.getCurrent().getLocale();

    public AdminChangePasswordDialog(Long userID) {
        this.gridUserId = userID;

        H2 changeHeader = new H2(getTranslation("view.adminChangePw.changeHeader", locale));
        Text text = new Text(getTranslation("view.adminChangePw.textSure", locale));
        PasswordField newPasswordField = new PasswordField(getTranslation("view.adminChangePw.newPassword", locale));
        VerticalLayout layout = new VerticalLayout();
        layout.add(changeHeader, text, newPasswordField);
        add(layout);
        Button changeButton = new Button(getTranslation("view.adminChangePw.button.change", locale));
        changeButton.addClickListener(clickEvent -> {
            UserService.getInstance().setNewPassword(PasswordHasher.hash(newPasswordField.getValue()), gridUserId);
            close();
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        changeButton.getStyle().set("margin-right", "auto");
        getFooter().add(changeButton);
        Button cancelButton = new Button(getTranslation("view.adminChangePw.button.cancel", locale), (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(cancelButton);
        Button change = new Button(getTranslation("view.adminChangePw.button.changePw", locale), (e) -> open());
    }
}
