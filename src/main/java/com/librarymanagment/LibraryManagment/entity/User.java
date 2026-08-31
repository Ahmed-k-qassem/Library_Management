package com.librarymanagment.LibraryManagment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 5, max = 20)
    private String username;

    @NotNull
    @Column(name = "keycloak_user_id", nullable = false)
    private String keycloakUserId;

    private String role;

    public User() {
        username = null;
        keycloakUserId = null;
        role = null;
    }

    public User(String username, String keycloakUserId, String role) {
        this.username = username;
        this.keycloakUserId = keycloakUserId;
        this.role = role;
    }

    public User(long id, String username, String keycloakUserId, String role) {
        this.id = id;
        this.username = username;
        this.keycloakUserId = keycloakUserId;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKeycloakUserId() {
        return keycloakUserId;
    }

    public void setKeycloakUserId(String keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}