package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.AuthorResponseDTO;

public class AuthorResponseDtoTestDataBuilder {
    private Long id = 1L;
    private String name = "default author";
    private String nationality = "Unknown";

    public static AuthorResponseDtoTestDataBuilder anAuthorResponseDto(){
        return new AuthorResponseDtoTestDataBuilder();
    }

    public AuthorResponseDtoTestDataBuilder withId(Long id){
        this.id = id;
        return this;
    }

    public AuthorResponseDtoTestDataBuilder withName(String name){
        this.name = name;
        return this;
    }


    public AuthorResponseDtoTestDataBuilder withNationality(String nationality){
        this.nationality = nationality;
        return this;
    }

    public AuthorResponseDTO build(){
        return new AuthorResponseDTO(id,name,nationality);
    }
}
