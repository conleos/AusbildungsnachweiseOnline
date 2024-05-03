package com.conleos.data.entity;

import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name = "formId_FK", referencedColumnName = "id")
    Form form;

    @Column(name = "comment")
    String comment;

    @ManyToOne
    @JoinColumn(name = "userId_FK", referencedColumnName = "id")
    User userId;

    @Column(name = "time")
    String time;

    public Long getId() {
        return id;
    }

    public Form getForm() {
        return form;
    }

    public String getComment() {
        return comment;
    }

    public User getUserId() {
        return userId;
    }

    public String getTime() {
        return time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
