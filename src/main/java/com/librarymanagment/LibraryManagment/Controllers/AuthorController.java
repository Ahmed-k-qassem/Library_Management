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
    private final ObjectMapper objectMapper;
    public AuthorController(AuthorService authorService, ObjectMapper objectMapper) {
        this.authorService = authorService;
        this.objectMapper = objectMapper;

    }

    @GetMapping
    public List<AuthorResponseDTO> getAuthors(){
        return authorService.findAll()
                .stream()
                .map(authorService::castToAuthorResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable long id){
        AuthorResponseDTO responseDTO = authorService.castToAuthorResponseDTO(authorService.findById(id));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<AuthorResponseDTO> addAuthor(@Valid @RequestBody AuthorRequestDTO dto){
        Author savedAuthor = authorService.saveAuthor(dto);
        AuthorResponseDTO responseDTO= authorService.castToAuthorResponseDTO(savedAuthor);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> putAuthor(@PathVariable long id, @Valid @RequestBody AuthorRequestDTO requestDTO){
        Author existing = authorService.findById(id);
        existing.setAuthorName(requestDTO.authorName());
        existing.setNationality(requestDTO.nationality());
        AuthorResponseDTO responseDTO = authorService.castToAuthorResponseDTO(authorService.saveAuthor(existing));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable long id){
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<AuthorResponseDTO> patchAuthor(@PathVariable long id, @RequestBody String patchBody) {

        Author targetAuthor = authorService.findById(id);


        AuthorResponseDTO responseDTO = applyPatchToAuthor(patchBody, authorService.castToAuthorResponseDTO(targetAuthor));


        return ResponseEntity.ok(authorService.castToAuthorResponseDTO(authorService.saveAuthor(responseDTO)));
    }


    private AuthorResponseDTO applyPatchToAuthor(String patchBody, AuthorResponseDTO targetAuthor) {
        try {
            JsonNode patchNode = objectMapper.readTree(patchBody);
            JsonNode targetNode = objectMapper.convertValue(targetAuthor, JsonNode.class);

            JsonNode patchedNode = JsonPatch.apply(patchNode, targetNode);

            return objectMapper.treeToValue(patchedNode, AuthorResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new JsonPatchProcessingException("Invalid patch format: " + e.getMessage());
        }
    }

}