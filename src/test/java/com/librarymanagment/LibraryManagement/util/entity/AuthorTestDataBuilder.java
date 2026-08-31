package com.librarymanagment.LibraryManagement.util.entity;

import com.librarymanagment.LibraryManagement.entity.Author;

public class AuthorTestDataBuilder {

    private Long id = 1L;
    private String authorName = "default author";
    private String nationality = "Unknown";


    public static AuthorTestDataBuilder anAuthor() {
        return new AuthorTestDataBuilder();
    }

    public AuthorTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AuthorTestDataBuilder withAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public AuthorTestDataBuilder withNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public Author build() {
        return new Author(id, authorName, nationality);
    }
}