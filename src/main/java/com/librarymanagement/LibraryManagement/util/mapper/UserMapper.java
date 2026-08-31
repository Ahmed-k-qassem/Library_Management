package com.librarymanagement.LibraryManagement.util.mapper;

import com.librarymanagement.LibraryManagement.entity.User;
import com.librarymanagement.LibraryManagement.dto.Request.UserRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO mapUserToResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole(), user.getKeycloakUserId());
    }

    public User mapUserRequestDTOtoUser(UserRequestDTO dto, String role, String keycloakUserId) {
        return new User(dto.username(), keycloakUserId, role);
    }
}