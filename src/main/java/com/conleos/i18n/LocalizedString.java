package com.conleos.i18n;

import com.vaadin.flow.component.UI;

public class LocalizedString {

    private String key;

    public LocalizedString(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return TranslationProvider.getInstance().getTranslation(key, UI.getCurrent().getLocale());
    }

}
