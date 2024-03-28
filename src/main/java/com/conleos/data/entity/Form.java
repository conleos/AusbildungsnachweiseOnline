package com.conleos.data.entity;

import com.conleos.views.form.KindOfWork;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Form {

    @Entity
    public class FormEntry {

        @Id
        private Long id;

        LocalDate date;

        LocalTime begin;
        LocalTime end;

        String description;

        KindOfWork kindOfWork;

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

    @OneToMany
    List<FormEntry> entries;

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

    public List<FormEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<FormEntry> entries) {
        this.entries = entries;
    }
    public void addEntry(FormEntry entry) {
        this.entries.add(entry);
    }
}
