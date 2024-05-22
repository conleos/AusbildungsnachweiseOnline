package com.conleos.data.entity;

import com.conleos.data.service.UserService;
import com.conleos.views.form.KindOfWork;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
public class Form {

    @Entity
    public static class FormEntry {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        LocalTime begin = LocalTime.of(0, 0);

        LocalTime end = LocalTime.of(0, 0);
        int pauseInMinutes = 0;
        String description = "";
        KindOfWork kindOfWork = KindOfWork.PracticalWork;

        public FormEntry() {
        }

        public FormEntry(FormEntry other) {
            if (other != null) {
                // Do not copy ID
                this.begin = other.begin;
                this.end = other.end;
                this.pauseInMinutes = other.pauseInMinutes;
                this.description = other.description;
                this.kindOfWork = other.kindOfWork;
            }
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public LocalTime getBegin() {
            return begin;
        }

        public void setBegin(LocalTime begin) {
            this.begin = begin;
        }

        public int getPause() {
            return pauseInMinutes;
        }

        public void setPause(int pause) {
            this.pauseInMinutes = pause;
        }

        public LocalTime getEnd() {
            return end;
        }

        public void setEnd(LocalTime end) {
            this.end = end;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public KindOfWork getKindOfWork() {
            return kindOfWork;
        }

        public void setKindOfWork(KindOfWork kindOfWork) {
            this.kindOfWork = kindOfWork;
        }
    }


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    User owner;

    LocalDate mondayOfThatWeek;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry monday = new FormEntry();
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry tuesday = new FormEntry();
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry wednesday = new FormEntry();
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry thursday = new FormEntry();
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry friday = new FormEntry();
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry saturday = new FormEntry();
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    FormEntry sunday = new FormEntry();

    FormStatus status = FormStatus.InProgress;
    Boolean newAction = false;

    Long userWhoSignedOrRejected;

    public Form(User owner, LocalDate date) {
        mondayOfThatWeek = date;
        this.owner = owner;
    }

    protected Form() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDate getMondayDate() {
        return mondayOfThatWeek;
    }

    public Boolean getNewAction() {
        return newAction;
    }

    public void setNewAction(Boolean newAction) {
        this.newAction = newAction;
    }

    public FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormStatus status) {
        this.status = status;
    }

    public FormEntry getEntryByDate(LocalDate date) {
        return getEntryByDayOffset((int) DAYS.between(mondayOfThatWeek, date));
    }

    private FormEntry getEntryByDayOffset(int dayOffset) {
        switch (dayOffset) {
            case 0 -> {
                return monday;
            }
            case 1 -> {
                return tuesday;
            }
            case 2 -> {
                return wednesday;
            }
            case 3 -> {
                return thursday;
            }
            case 4 -> {
                return friday;
            }
            case 5 -> {
                return saturday;
            }
            case 6 -> {
                return sunday;
            }
            default -> {
                return null;
            }
        }
    }

    public FormEntry getMonday() {
        return monday;
    }

    public void setMonday(FormEntry monday) {
        this.monday = monday;
    }

    public FormEntry getTuesday() {
        return tuesday;
    }

    public void setTuesday(FormEntry tuesday) {
        this.tuesday = tuesday;
    }

    public FormEntry getWednesday() {
        return wednesday;
    }

    public void setWednesday(FormEntry wednesday) {
        this.wednesday = wednesday;
    }

    public FormEntry getThursday() {
        return thursday;
    }

    public void setThursday(FormEntry thursday) {
        this.thursday = thursday;
    }

    public FormEntry getFriday() {
        return friday;
    }

    public void setFriday(FormEntry friday) {
        this.friday = friday;
    }

    public FormEntry getSaturday() {
        return saturday;
    }

    public void setSaturday(FormEntry saturday) {
        this.saturday = saturday;
    }

    public FormEntry getSunday() {
        return sunday;
    }

    public void setSunday(FormEntry sunday) {
        this.sunday = sunday;
    }

    public User getUserWhoSignedOrRejected() {
        return UserService.getInstance().getUserByID(this.userWhoSignedOrRejected);
    }

    public void setUserWhoSignedOrRejected(User userWhoSignedOrRejected) {
        this.userWhoSignedOrRejected = userWhoSignedOrRejected == null ? -1 : userWhoSignedOrRejected.getId();
    }
}
