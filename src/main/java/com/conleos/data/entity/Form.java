package com.conleos.data.entity;

import com.conleos.views.form.KindOfWork;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Form {

    @Entity
    public static class FormEntry {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        LocalDate date;

        LocalTime begin;
        LocalTime end;

        String description;

        KindOfWork kindOfWork;

        public FormEntry() {

        }
        public FormEntry(FormEntry other) {
            this.date = other.date;
            this.begin = other.begin;
            this.end = other.end;
            this.description = other.description;
            this.kindOfWork = other.kindOfWork;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getBegin() {
            return begin;
        }

        public void setBegin(LocalTime begin) {
            this.begin = begin;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    List<FormEntry> entries;

    FormStatus status = FormStatus.InProgress;

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
    public FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormStatus status) {
        this.status = status;
    }

    public List<FormEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<FormEntry> entries) {
        this.entries = entries;
    }
    public void addEntry(FormEntry entry) {
        this.entries.add(entry);
    }
    public void removeAllEntries() {
        this.entries.clear();
    }
    public void addEntries(List<FormEntry> entries) {
        for (FormEntry entry : entries) {
            addEntry(entry);
        }
    }
    public List<FormEntry> getEntriesByDate(LocalDate date) {
        List<FormEntry> result = new ArrayList<>();

        for (FormEntry entry : entries) {
            if (entry.getDate().equals(date)) {
                result.add(entry);
            }
        }

        return result;
    }
}
