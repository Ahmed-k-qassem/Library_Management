package com.librarymanagement.LibraryManagement.user;

import org.springframework.stereotype.Component;

@Component
class UserMapper {

    public UserResponseDTO mapUserToResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole(), user.getKeycloakUserId());
    }
}