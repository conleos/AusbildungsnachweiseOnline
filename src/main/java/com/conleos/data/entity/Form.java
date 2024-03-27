package com.conleos.data.entity;

import com.conleos.common.Role;
import com.conleos.views.form.KindOfWork;
import jakarta.persistence.*;
import org.hibernate.annotations.Struct;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Form {

    @Entity
    public static class FormResult {

        @Id
        private Long id;

        String description;

        KindOfWork kindOfWork;

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
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
    List<FormResult> results;

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

    public List<FormResult> getResults() {
        return results;
    }

    public void setResults(List<FormResult> results) {
        this.results = results;
    }
    public void addResult(FormResult result) {
        this.results.add(result);
    }
}
