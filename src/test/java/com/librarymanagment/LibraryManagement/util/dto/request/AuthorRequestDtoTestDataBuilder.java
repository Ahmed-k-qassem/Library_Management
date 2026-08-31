package com.librarymanagment.LibraryManagement.util.dto.request;


import com.librarymanagment.LibraryManagement.dto.Request.AuthorRequestDTO;

public class AuthorRequestDtoTestDataBuilder {
    private String name = "default author";
    private String nationality = "Unknown";

    public static AuthorRequestDtoTestDataBuilder anAuthorRequestDTO(){
        return new AuthorRequestDtoTestDataBuilder();
    }

    public AuthorRequestDtoTestDataBuilder withName(String name){
        this.name = name;
        return this;
    }

    public AuthorRequestDtoTestDataBuilder withNationality(String nationality){
        this.nationality = nationality;
        return this;
    }

    public AuthorRequestDTO build(){
        return new AuthorRequestDTO(name, nationality);
    }
}
