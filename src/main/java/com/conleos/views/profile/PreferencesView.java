package com.conleos.views.profile;

import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import java.util.Objects;

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

        ComboBox<String> themeBox = new ComboBox<>("Theme");
        themeBox.setItems(new String[]{"☀\uFE0F Bright", "\uD83C\uDF19 Dark"});
        if(UI.getCurrent().getElement().getThemeList().isEmpty()){
            UI.getCurrent().getElement().getThemeList().add("☀\uFE0F Bright");
        }
        themeBox.setValue(UI.getCurrent().getElement().getThemeList().stream().toList().get(0) + " " + UI.getCurrent().getElement().getThemeList().stream().toList().get(1));
        themeBox.addValueChangeListener(e -> {
            setTheme(Objects.equals(e.getValue(), "\uD83C\uDF19 Dark"));
        });

        return new VerticalLayout(langBox, themeBox);
    }

    /**
     * function to switch theme(darkmode on off)
     * @param dark
     */
    private static void setTheme(boolean dark) {
        var js = "document.documentElement.setAttribute('theme', $0)";
        UI.getCurrent().getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);

        if(dark){
            UI.getCurrent().getElement().getThemeList().remove("☀\uFE0F");
            UI.getCurrent().getElement().getThemeList().remove("Bright");
            UI.getCurrent().getElement().getThemeList().add("\uD83C\uDF19 Dark");
        }
        else {
            UI.getCurrent().getElement().getThemeList().remove("\uD83C\uDF19");
            UI.getCurrent().getElement().getThemeList().remove("Dark");
            UI.getCurrent().getElement().getThemeList().add("☀\uFE0F Bright");
        }
    }

}