package com.librarymanagement.LibraryManagement.category;

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

    public CategoryTestDataBuilder withoutId(){
        this.id = null;
        return this;
    }

    public CategoryTestDataBuilder withName(String name){
        this.name = name;
        return this;
    }

    public Category build(){
        return id == null ? new Category(name) : new Category(id, name);
    }
}