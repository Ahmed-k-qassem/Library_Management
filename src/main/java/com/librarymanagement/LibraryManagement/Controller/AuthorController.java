package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Request.AuthorRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.AuthorResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import com.librarymanagement.LibraryManagement.dto.doc.JsonPatchOperationDTO;
import com.librarymanagement.LibraryManagement.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @Operation(
            summary = "List all authors",
            description = "Returns every author in the catalogue. Open to any authenticated user.")
    @ApiResponse(
            responseCode = "200",
            description = "The full list of authors, possibly empty",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = AuthorResponseDTO.class))))
    public List<AuthorResponseDTO> getAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get one author by id",
            description = "Open to any authenticated user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The author was found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No author exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<AuthorResponseDTO> getAuthorById(
            @Parameter(description = "Database id of the author", example = "1", required = true)
            @PathVariable long id) {
        return new ResponseEntity<>(authorService.findByIdToResponse(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Create an author",
            description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "The author was created and is returned with its generated id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bean-validation failed. The body is a flat map of field name to message.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object", description = "field name -> validation message"),
                            examples = @ExampleObject(value = """
                                    {
                                      "authorName": "please enter author name",
                                      "nationality": "Nationality cannot have numbers."
                                    }""")))
    })
    public ResponseEntity<AuthorResponseDTO> addAuthor(@Valid @RequestBody AuthorRequestDTO dto) {
        return new ResponseEntity<>(authorService.saveAuthor(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Replace an author",
            description = """
                    Full replacement: every field in the payload is written, so omitted fields
                    are not preserved. Use PATCH when you only want to change one field.

                    Requires the **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The author was replaced",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bean-validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(
                    responseCode = "404",
                    description = "No author exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<AuthorResponseDTO> putAuthor(
            @Parameter(description = "Database id of the author to replace", example = "1", required = true)
            @PathVariable long id,
            @Valid @RequestBody AuthorRequestDTO requestDTO) {
        return new ResponseEntity<>(authorService.updateAuthor(id, requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an author",
            description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The author was deleted; no body is returned"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No author exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(description = "Database id of the author to delete", example = "1", required = true)
            @PathVariable long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @Operation(
            summary = "Partially update an author (RFC 6902 JSON Patch)",
            description = """
                    Send a JSON Patch document with `Content-Type: application/json-patch+json`.
                    Patchable pointers are `/authorName` and `/nationality`.

                    Requires the **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The patch applied cleanly; the updated author is returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "The patch document is malformed or points at an unknown field",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No author exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<AuthorResponseDTO> patchAuthor(
            @Parameter(description = "Database id of the author to patch", example = "1", required = true)
            @PathVariable long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "An RFC 6902 JSON Patch document: an array of operations.",
                    content = @Content(
                            mediaType = "application/json-patch+json",
                            array = @ArraySchema(schema = @Schema(implementation = JsonPatchOperationDTO.class)),
                            examples = {
                                    @ExampleObject(
                                            name = "Change nationality",
                                            value = """
                                                    [
                                                      { "op": "replace", "path": "/nationality", "value": "Syrian" }
                                                    ]"""),
                                    @ExampleObject(
                                            name = "Change both fields",
                                            value = """
                                                    [
                                                      { "op": "replace", "path": "/authorName", "value": "Ghassan Kanafani" },
                                                      { "op": "replace", "path": "/nationality", "value": "Palestinian" }
                                                    ]""")
                            }))
            @RequestBody String patchBody) {
        return new ResponseEntity<>(authorService.patchAuthor(id, patchBody), HttpStatus.OK);
    }
}