package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Request.CategoryRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.CategoryResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import com.librarymanagement.LibraryManagement.dto.doc.JsonPatchOperationDTO;
import com.librarymanagement.LibraryManagement.service.CategoryService;
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
@RequestMapping("/api/categories")
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(
            summary = "List all categories",
            description = "Requires the **ADMIN** realm role — every endpoint under /api/categories does.")
    @ApiResponse(
            responseCode = "200",
            description = "All categories, possibly empty",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class))))
    public List<CategoryResponseDTO> getCategoryList() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one category by id", description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The category was found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No category exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @Parameter(description = "Database id of the category", example = "1", required = true)
            @PathVariable long id) {
        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryResponseById(id);
        return new ResponseEntity<>(categoryResponseDTO, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a category", description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "The category was created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "The name was blank, or a category with that name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<CategoryResponseDTO> saveCategory(@Valid @RequestBody CategoryRequestDTO category) {
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a category", description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The category was replaced",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "The name was blank",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(
                    responseCode = "404",
                    description = "No category exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @Parameter(description = "Database id of the category to replace", example = "1", required = true)
            @PathVariable long id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        return new ResponseEntity<>(categoryService.updateCategory(id, requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The category was deleted; no body is returned"),
            @ApiResponse(
                    responseCode = "400",
                    description = "The category cannot be deleted because books still reference it",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No category exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Database id of the category to delete", example = "1", required = true)
            @PathVariable long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @Operation(
            summary = "Partially update a category (RFC 6902 JSON Patch)",
            description = """
                    Send a JSON Patch document with `Content-Type: application/json-patch+json`.
                    The only patchable pointer is `/name`.

                    Requires the **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The patch applied cleanly",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "The patch document is malformed or points at an unknown field",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No category exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<CategoryResponseDTO> patchCategory(
            @Parameter(description = "Database id of the category to patch", example = "1", required = true)
            @PathVariable long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "An RFC 6902 JSON Patch document: an array of operations.",
                    content = @Content(
                            mediaType = "application/json-patch+json",
                            array = @ArraySchema(schema = @Schema(implementation = JsonPatchOperationDTO.class)),
                            examples = @ExampleObject(
                                    name = "Rename the category",
                                    value = """
                                            [
                                              { "op": "replace", "path": "/name", "value": "Science Fiction" }
                                            ]""")))
            @RequestBody String patch) {
        return new ResponseEntity<>(categoryService.patchCategory(id, patch), HttpStatus.OK);
    }
}