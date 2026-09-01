package com.librarymanagement.LibraryManagement.util.entity;
public class PatchBodyBuilder {
    private String targetAttribute = "attribute to change";
    private String changedTo = "value of change";
    private PatchBodyBuilder(){

    }

    public static PatchBodyBuilder getInstance(){
        return new PatchBodyBuilder();
    }


    public PatchBodyBuilder targetColumn(String attribute){
        this.targetAttribute = attribute;
        return this;
    }

    public PatchBodyBuilder columnValue(String value){
        this.changedTo = value;
        return this;
    }

    public String build(){
        return "[{\"op\":\"replace\",\"path\":\"/%s\",\"value\":\"%s\"}]".formatted(targetAttribute, changedTo);
    }
}
