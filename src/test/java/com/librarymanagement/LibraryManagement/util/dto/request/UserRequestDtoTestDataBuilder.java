package com.librarymanagement.LibraryManagement.util.dto.request;

import com.librarymanagement.LibraryManagement.dto.Request.UserRequestDTO;

public class UserRequestDtoTestDataBuilder {
    private String username = "default_user";

    private UserRequestDtoTestDataBuilder(){

    }

    public static UserRequestDtoTestDataBuilder getInstance(){
        return new UserRequestDtoTestDataBuilder();
    }

    public UserRequestDtoTestDataBuilder withUsername(String username){
        this.username = username;
        return this;
    }

    public UserRequestDTO build(){
        return new UserRequestDTO(username);
    }
}
