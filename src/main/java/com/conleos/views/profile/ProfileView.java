package com.conleos.views.profile;

import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

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

        return new VerticalLayout(firstName, lastName, new Button("Reset Password"));
    }

}