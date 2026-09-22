package com.librarymanagement.LibraryManagement.common.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.common.GenericPatcher;

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
