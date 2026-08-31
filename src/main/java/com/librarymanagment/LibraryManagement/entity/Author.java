package com.librarymanagment.LibraryManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "please enter author name")
    private String authorName;
    @NotBlank(message = "please select nationality")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Nationality cannot have numbers.")
    private String nationality;

    public Author() {
        authorName = null;
        nationality = null;
    }

    public Author(String authorName, String nationality) {
        this.authorName = authorName;
        this.nationality = nationality;
    }

    public Author(long id, String authorName, String nationality) {
        this.id = id;
        this.authorName = authorName;
        this.nationality = nationality;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
