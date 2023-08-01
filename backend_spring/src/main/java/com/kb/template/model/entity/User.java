package com.kb.template.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idUser", nullable = false)
    private long id;

    @Column(name = "name")
    @NotBlank
    private String name = "";

    @Column(name = "avatarUrl")
    private String avatarUrl;

    @Column(name = "email", unique = true)
    @NotBlank
    private String email = "";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Config> configs;

    public User() {
    }

    public User(String name, String avatarUrl, String email) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return id == that.id && name.equals(that.name) && Objects.equals(avatarUrl, that.avatarUrl) && email.equals(that.email) && Objects.equals(configs, that.configs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, avatarUrl, email, configs);
    }
}
