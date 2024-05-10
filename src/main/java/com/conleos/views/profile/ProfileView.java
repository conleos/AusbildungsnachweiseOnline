package com.conleos.views.profile;

import com.conleos.common.PasswordHasher;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.i18n.TranslationProvider;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.Locale;

@PageTitle("Edit your profile")
@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout{

    private Locale locale = UI.getCurrent().getLocale();;
    private TranslationProvider translationProvider;

    public ProfileView(UserService service,TranslationProvider translationProvider) {
        this.translationProvider = translationProvider;
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        add(createContent(session.getUser()));

    }

    private Component createContent(User user) {
        TextField firstName = new TextField(translationProvider.getTranslation("view.profile.label.firstname", locale));
        firstName.setValue(user.getFirstName());
        firstName.setEnabled(false);
        TextField lastName = new TextField(translationProvider.getTranslation("view.profile.label.lastname", locale));
        lastName.setValue(user.getLastName());
        lastName.setEnabled(false);
        Dialog dialog = new Dialog();
        H2 changeHeader = new H2(translationProvider.getTranslation("view.profile.button.changePassword", locale));
        Text sure = new Text(translationProvider.getTranslation("view.profile.textsure", locale));
        PasswordField oldPasswordField = new PasswordField(translationProvider.getTranslation("view.profile.label.oldPassword", locale));
        PasswordField newPasswordField = new PasswordField(translationProvider.getTranslation("view.profile.label.newPassword", locale));
        Text wrong = new Text(translationProvider.getTranslation("view.profile.textwrong", locale));
        VerticalLayout input = new VerticalLayout();
        input.add(changeHeader,sure,oldPasswordField,newPasswordField);
        dialog.add(input);
        Button changeButton = new Button(translationProvider.getTranslation("view.profile.button.change", locale));
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
        Button cancelButton = new Button(translationProvider.getTranslation("view.profile.button.cancel", locale), (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        Button change = new Button(translationProvider.getTranslation("view.profile.button.changePassword", locale), e -> dialog.open());
        return new VerticalLayout(firstName, lastName, dialog, change);
    }

}