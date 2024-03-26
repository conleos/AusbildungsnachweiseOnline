package com.conleos.data.entity;

import com.conleos.common.Role;
import jakarta.persistence.*;

@Entity
public class Form {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    User owner;

    public Form(User owner) {
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
}
