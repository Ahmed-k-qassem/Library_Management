package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.Repostries.UserRepository;
import com.librarymanagment.LibraryManagment.dto.Request.UserRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
import com.librarymanagment.LibraryManagment.util.mapper.UserMapper;
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
        return userRepository.findByUsername(username);
    }






    public List<UserResponseDTO> findAll(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapUserToResponseDTO)
                .toList();
    }
}
