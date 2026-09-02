package com.librarymanagement.LibraryManagement.service;

import com.librarymanagement.LibraryManagement.entity.User;
import com.librarymanagement.LibraryManagement.repository.UserRepository;
import com.librarymanagement.LibraryManagement.dto.Response.UserResponseDTO;
import com.librarymanagement.LibraryManagement.util.mapper.UserMapper;
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

    public User findByKeycloakUserId(String keycloakUserId) {
        return userRepository.findByKeycloakUserId(keycloakUserId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public void syncUser(String uuid, String username, String role) {

        if (!userRepository.existsByKeycloakUserId(uuid)) {
            User newUser = new User();
            newUser.setKeycloakUserId(uuid);
            newUser.setUsername(username);
            newUser.setRole(role);

            userRepository.save(newUser);
            System.out.println("Provisioned new user: " + username);
        }
    }
}