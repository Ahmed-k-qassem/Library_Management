package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.CategoryResponseDTO;

public class CategoryResponseDtoTestDataBuilder {
    private Long id = 1L;
    private String name = "default";

    private CategoryResponseDtoTestDataBuilder(){

    }

    public static CategoryResponseDtoTestDataBuilder getInstance(){
        return new CategoryResponseDtoTestDataBuilder();
    }


    public CategoryResponseDtoTestDataBuilder withId(Long id){
        this.id = id;
        return this;
    }


    public CategoryResponseDtoTestDataBuilder withName(String name){
        this.name = name;
        return this;
    }

    public CategoryResponseDTO build(){
        return new CategoryResponseDTO(id,name);
    }
}
