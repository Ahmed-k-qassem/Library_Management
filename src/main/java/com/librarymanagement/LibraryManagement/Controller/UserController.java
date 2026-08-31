package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.dto.Response.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> getUsers(){
        return userService.findAll();
    }


    @DeleteMapping("admin/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable String uuid){
        userService.deleteUserByKeycloakId(uuid);
        return ResponseEntity.noContent().build();
    }


}
