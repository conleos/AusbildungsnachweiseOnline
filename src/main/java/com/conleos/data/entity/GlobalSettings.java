package com.conleos.data.entity;

import jakarta.persistence.*;

@Entity
public class GlobalSettings {

    @Id
    Long id = 1L;

    Double hoursInAWeek = 40.0;

    public Long getId() {
        return id;
    }

    public Double getHoursInAWeek() {
        return hoursInAWeek;
    }

    public void setHoursInAWeek(Double hoursInAWeek) {
        this.hoursInAWeek = hoursInAWeek;
    }
}
