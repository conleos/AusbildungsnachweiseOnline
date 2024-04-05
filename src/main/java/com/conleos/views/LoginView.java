package com.conleos.views;

import com.conleos.common.PasswordHasher;
import com.conleos.core.Session;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("/login")
public class LoginView extends VerticalLayout {

    TextField username;
    PasswordField password;
    private Span logInFailedMessage;

    public LoginView() {
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        getElement().getStyle().set("height", "100%");
        add(new H1("Login as an existing User"));

        username = new TextField("Username");
        password = new PasswordField("Password");
        add(username);
        add(password);

        Button login = new Button("Login");
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClickShortcut(Key.ENTER);
        login.addClickListener(clickEvent -> {
            Session session = Session.authenticateUserAndCreateSession(username.getValue(), PasswordHasher.hash(password.getValue()));
            if (session != null) {
                UI.getCurrent().navigate("");
                //UI.getCurrent().getPage().reload();
            } else {
                if (logInFailedMessage == null) {
                    logInFailedMessage = new Span("Failed to Log in!");
                    add(logInFailedMessage);
                }
            }
        });
        add(login);
    }
}