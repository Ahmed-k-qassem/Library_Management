package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.Repostries.UserRepository;
import com.librarymanagment.LibraryManagment.dto.Request.UserRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
import com.librarymanagment.LibraryManagment.util.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        User user = userMapper.mapUserRequestDTOtoUser(userRequestDTO, "USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return userMapper.mapUserToResponseDTO(user);
    }


    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    @Transactional
    @PreAuthorize("#id == authentication.getPrincipal().getId() Or hasRole('ADMIN')")
    public void deleteUserById(long id){
        int deleted = userRepository.deleteUserById(id);
        if(deleted == 0){
            throw new EntityNotFoundException("User not found");
        }
    }



    public List<UserResponseDTO> findAll(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapUserToResponseDTO)
                .toList();
    }
}
