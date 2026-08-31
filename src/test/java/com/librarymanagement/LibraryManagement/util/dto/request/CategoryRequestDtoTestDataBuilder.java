package com.librarymanagement.LibraryManagement.util.dto.request;

import com.librarymanagement.LibraryManagement.dto.Request.CategoryRequestDTO;

public class CategoryRequestDtoTestDataBuilder {
    private String name = "default";

    private CategoryRequestDtoTestDataBuilder(){

    }

    public static CategoryRequestDtoTestDataBuilder getInstance(){
        return new CategoryRequestDtoTestDataBuilder();
    }

    public CategoryRequestDtoTestDataBuilder withName(String name){
        this.name = name;
        return this;
    }

    public CategoryRequestDTO build(){
        return new CategoryRequestDTO(name);
    }
}
