package com.librarymanagement.LibraryManagement.util.entity;

import com.librarymanagement.LibraryManagement.entity.User;

public class UserTestDataBuilder {
    private Long id = 1L;
    private String username = "defaultUser";
    private String keycloakUserId = "b7f1c2d4-0000-4aaa-9000-000000000000";
    private String role = "USER";

    private UserTestDataBuilder(){

    }

    public static UserTestDataBuilder getInstance(){
        return new UserTestDataBuilder();
    }

    public UserTestDataBuilder withId(Long id){
        this.id = id;
        return this;
    }

    public UserTestDataBuilder withoutId(){
        this.id = null;
        return this;
    }

    public UserTestDataBuilder withUsername(String username){
        this.username = username;
        return this;
    }

    public UserTestDataBuilder withKeycloakUserId(String keycloakUserId){
        this.keycloakUserId = keycloakUserId;
        return this;
    }

    public UserTestDataBuilder withRole(String role){
        this.role = role;
        return this;
    }

    public User build(){
        return id == null
                ? new User(username, keycloakUserId, role)
                : new User(id, username, keycloakUserId, role);
    }
}