package com.librarymanagment.LibraryManagment.util.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagment.LibraryManagment.util.GenericPatcher;

public class GenericPatcherTestDataBuilder {
    private ObjectMapper objectMapper = new ObjectMapper();
    private GenericPatcherTestDataBuilder(){
        new GenericPatcherTestDataBuilder();
    }

    public static GenericPatcherTestDataBuilder getInstance(){
        return new GenericPatcherTestDataBuilder();
    }

    public GenericPatcher build(){
        return new GenericPatcher(objectMapper);
    }
}
