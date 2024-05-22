package com.conleos.i18n;

import com.vaadin.flow.component.UI;

public class Lang {

    private Lang() {
    }

    public static String translate(String key) {
        return TranslationProvider.getInstance().getTranslation(key, UI.getCurrent().getLocale());
    }

}
