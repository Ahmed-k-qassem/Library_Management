package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.Repostries.UserRepository;
import com.librarymanagment.LibraryManagment.dto.Request.UserRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    @Transactional
    public User save(UserRequestDTO userRequestDTO) {
        User  user = new User();
        user.setUsername(userRequestDTO.username());
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        user.setRole("USER");
        return userRepository.save(user);
    }


    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }


    public UserResponseDTO castUserToResponseDTO(User user){
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole());
    }



    public List<User> findAll(){
        return userRepository.findAll();
    }
}
