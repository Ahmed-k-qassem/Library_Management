package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.Repostries.UserRepository;
import com.librarymanagment.LibraryManagment.dto.Request.UserRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
import com.librarymanagment.LibraryManagment.util.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO, String keycloakUserId) {
        User user = userMapper.mapUserRequestDTOtoUser(userRequestDTO, "USER", keycloakUserId);
        user = userRepository.save(user);
        return userMapper.mapUserToResponseDTO(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    // Simplified SpEL expression: 'authentication.name' resolves directly to the JWT 'sub' claim
    @PreAuthorize("#keycloakUserId == authentication.name or hasRole('ADMIN')")
    public void deleteUserByKeycloakId(String keycloakUserId) {
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapUserToResponseDTO)
                .toList();
    }

}