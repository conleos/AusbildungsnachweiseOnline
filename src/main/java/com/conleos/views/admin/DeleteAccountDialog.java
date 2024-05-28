package com.conleos.views.admin;

import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.i18n.Lang;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DeleteAccountDialog extends Dialog {

    public DeleteAccountDialog(Long userID) {
        User account = UserService.getInstance().getUserByID(userID);

        H2 changeHeader = new H2(Lang.translate("view.deleteAccount.changeHeader"));
        Text text = new Text(String.format(Lang.translate("view.deleteAccount.text") + " '%s' ?", account.getUsername()));
        TextField validateField = new TextField(String.format(Lang.translate("view.deleteAccount.validateField1") + " '%s' " + Lang.translate("view.deleteAccount.validateField2"), account.getUsername()));
        VerticalLayout layout = new VerticalLayout();
        layout.add(changeHeader, text, validateField);
        add(layout);
        Button deleteButton = new Button(Lang.translate("view.deleteAccount.button"), VaadinIcon.TRASH.create());
        deleteButton.addClickListener(clickEvent -> {
            if (account.getUsername().equals(validateField.getValue())) {
                UserService.getInstance().deleteUserAccountByID(account.getId());
                UI.getCurrent().getPage().reload();
            } else {
                Notification.show(Lang.translate("view.deleteAccount.notification"), 4000, Notification.Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        getFooter().add(deleteButton);
        Button cancelButton = new Button(Lang.translate("view.deleteAccount.button.cancel"), (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getFooter().add(cancelButton);
    }
}
