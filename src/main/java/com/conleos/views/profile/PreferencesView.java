package com.conleos.views.profile;

import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Edit your Preferences")
@Route(value = "preferences", layout = MainLayout.class)
public class PreferencesView extends VerticalLayout {

    public PreferencesView(UserService service) {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        add(createContent(session.getUser()));

    }

    private static Component createContent(User user) {

        ComboBox<String> langBox = new ComboBox<>("Language");
        langBox.setItems(new String[]{"\uD83C\uDDFA\uD83C\uDDF8 English", "\uD83C\uDDE9\uD83C\uDDEA German"});
        langBox.setValue("\uD83C\uDDFA\uD83C\uDDF8 English");

        return new VerticalLayout(langBox);
    }

}