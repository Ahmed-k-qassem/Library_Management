package com.librarymanagment.LibraryManagment.Controllers;

import com.librarymanagment.LibraryManagment.Services.UserService;
import com.librarymanagment.LibraryManagment.dto.Request.UserRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> getUsers(){
        return userService.findAll()
                .stream()
                .map(user -> userService.castUserToResponseDTO(user))
                .toList();
    }


    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO){
        return new ResponseEntity<>(userService.castUserToResponseDTO(userService.save(userRequestDTO)), HttpStatus.CREATED);
    }


}
