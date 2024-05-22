package com.conleos.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
public class TranslationProvider implements I18NProvider {

    private static TranslationProvider instance;
    public static final String BUNDLE_PREFIX = "translate";

    public final Locale LOCALE_EN = new Locale("en", "US");
    public final Locale LOCALE_DE = new Locale("de", "DE");

    private Map<String, ResourceBundle> localeMap;

    public TranslationProvider() {
        instance = this;
    }

    @PostConstruct
    private void initMap() {
        localeMap = new HashMap<>();

        for (final Locale locale : getProvidedLocales()) {

            final ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
            localeMap.put(locale.getLanguage(), resourceBundle);
        }
    }

    private List<Locale> locales = List.of(LOCALE_EN, LOCALE_DE);

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            LoggerFactory.getLogger(TranslationProvider.class.getName()).warn("Got lang request for key with null value!");
            return "";
        }

        String value;
        ResourceBundle resourceBundle = localeMap.get(locale.getLanguage());
        if (resourceBundle != null) {
            try {
                value = resourceBundle.getString(key);
            } catch (final MissingResourceException e) {
                LoggerFactory.getLogger(TranslationProvider.class.getName()).warn("Missing resource", e);
                return "!" + locale.getLanguage() + ": " + key;
            }
        } else {
            LoggerFactory.getLogger(TranslationProvider.class.getName()).warn("Resource bundle not found for locale: " + locale.getLanguage());
            return "!" + locale.getLanguage() + ": " + key;
        }

        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }

    public static TranslationProvider getInstance() {
        return instance;
    }
}
