package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.UserResponseDTO;

public class UserResponseDtoTestDataBuilder {
    private Long id = 1L;
    private String username = "default_name";
    private String keycloakUUID = "12233-19283-2939";
    private String role = "USER";

    private UserResponseDtoTestDataBuilder(){

    }

    public static UserResponseDtoTestDataBuilder getInstance(){
        return new UserResponseDtoTestDataBuilder();
    }

    public UserResponseDtoTestDataBuilder withId(Long id){
        this.id = id;
        return this;
    }


    public UserResponseDtoTestDataBuilder withUsername(String username){
        this.username =username;
        return this;
    }

    public UserResponseDtoTestDataBuilder withKeycloakUUID(String keycloakUUID){
        this.keycloakUUID = keycloakUUID;
        return this;
    }

    public UserResponseDtoTestDataBuilder withRole(String role){
        this.role = role;
        return this;
    }


    public UserResponseDTO build(){
        return new UserResponseDTO(id, username, role, keycloakUUID);
    }
}
