package com.librarymanagment.LibraryManagment.util.entity;
public class PatchBodyBuilder {
    private String attribute = "attribute to change";
    private String value = "value of change";
    private PatchBodyBuilder(){

    }

    public static PatchBodyBuilder getInstance(){
        return new PatchBodyBuilder();
    }


    public PatchBodyBuilder withAttribute(String attribute){
        this.attribute = attribute;
        return this;
    }

    public PatchBodyBuilder withValue(String value){
        this.value = value;
        return this;
    }

    public String build(){
        return "[{\\\"op\\\":\\\"replace\\\",\\\"path\\\":\\\"/%s\\\",\\\"value\\\":\\\"%s\\\"}]".formatted(attribute,value);
    }
}
