package com.librarymanagment.LibraryManagment.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flipkart.zjsonpatch.JsonPatch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagment.LibraryManagment.Entities.Author;
import com.librarymanagment.LibraryManagment.Services.AuthorService;
import com.librarymanagment.LibraryManagment.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.AuthorResponseDTO;
import com.librarymanagment.LibraryManagment.exception.JsonPatchProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {


    private final AuthorService authorService;
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;

    }

    @GetMapping
    public List<AuthorResponseDTO> getAuthors(){
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable long id){
        return new ResponseEntity<>(authorService.findByIdToResponse(id), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<AuthorResponseDTO> addAuthor(@Valid @RequestBody AuthorRequestDTO dto){
        return new ResponseEntity<>(authorService.saveAuthor(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> putAuthor(@PathVariable long id, @Valid @RequestBody AuthorRequestDTO requestDTO){

        return new ResponseEntity<>(authorService.updateAuthor(id, requestDTO), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable long id){
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping(value= "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<AuthorResponseDTO> patchAuthor(@PathVariable long id, @Valid @RequestBody String patchBody){
        return new ResponseEntity<>(authorService.patchAuthor(id, patchBody), HttpStatus.OK);
    }
}