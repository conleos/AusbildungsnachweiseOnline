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

    @Column(name = "nr")
    int nr;

    @Column(name = "description")
    String description;

    public Form(User owner,int nr,String description) {
        this.owner = owner;
        this.description = description;
        this.nr = nr;
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
