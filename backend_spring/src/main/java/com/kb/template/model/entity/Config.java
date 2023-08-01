package com.kb.template.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "config")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idConfig", nullable = false)
    private long id;

    @Column(name = "key", unique = true)
    private String key;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name="idUser", nullable = false)
    @JsonBackReference
    private User user;

    public Config(String key, String value, User user) {
        this.key = key;
        this.value = value;
        this.user = user;
    }

    public Config() {
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config that = (Config) o;
        return id == that.id && Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, value);
    }
}