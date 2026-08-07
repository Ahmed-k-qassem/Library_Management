package com.librarymanagment.LibraryManagment.util.mapper;

import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.dto.Request.UserRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
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