package com.conleos.data.repository;

import com.conleos.data.entity.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {

    @Query("Select gs from GlobalSettings gs")
    List<GlobalSettings> getAllGlobalSettings();

}
