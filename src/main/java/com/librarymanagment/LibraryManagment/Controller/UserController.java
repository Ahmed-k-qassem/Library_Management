package com.librarymanagment.LibraryManagment.Controller;

import com.librarymanagment.LibraryManagment.service.UserService;
import com.librarymanagment.LibraryManagment.dto.Response.UserResponseDTO;
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
