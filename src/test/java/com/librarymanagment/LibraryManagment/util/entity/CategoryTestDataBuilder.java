package com.librarymanagment.LibraryManagment.util.entity;

import com.librarymanagment.LibraryManagment.entity.Category;

public class CategoryTestDataBuilder {
    private Long id = 1L;
    private String name = "default";

    private CategoryTestDataBuilder(){

    }

    public static CategoryTestDataBuilder getInstance(){
        return new CategoryTestDataBuilder();
    }

    public CategoryTestDataBuilder withId(Long id){
        this.id = id;
        return this;
    }

    public CategoryTestDataBuilder withName(String name){
        this.name = name;
        return this;
    }

    public Category build(){
        return new Category(id,name);
    }
}
