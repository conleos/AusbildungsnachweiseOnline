package com.conleos.data.service;

import com.conleos.data.entity.GlobalSettings;
import com.conleos.data.repository.GlobalSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalSettingsService {
    private static GlobalSettingsService instance;
    private final GlobalSettingsRepository globalSettingsRepository;

    public GlobalSettingsService(GlobalSettingsRepository globalSettingsRepository) {
        instance = this;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public static GlobalSettingsService getInstance() {
        return instance;
    }

    public static GlobalSettings getGlobalSettings() {
        List<GlobalSettings> temp = getInstance().globalSettingsRepository.getAllGlobalSettings();

        if (!temp.isEmpty()) {
            return temp.getFirst();
        }

        GlobalSettings settings = new GlobalSettings();
        getInstance().globalSettingsRepository.save(settings);
        return settings;
    }

    public static void save(GlobalSettings globalSettings) {
        getInstance().globalSettingsRepository.save(globalSettings);
    }
}
