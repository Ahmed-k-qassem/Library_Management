package com.librarymanagment.LibraryManagment.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import com.librarymanagment.LibraryManagment.Entities.Category;
import com.librarymanagment.LibraryManagment.Services.CategoryService;
import com.librarymanagment.LibraryManagment.dto.Request.CategoryRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.CategoryResponseDTO;
import com.librarymanagment.LibraryManagment.exception.JsonPatchProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {


    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;
    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<CategoryResponseDTO> getCategoryList(){
        return categoryService.getAllCategories()
                .stream()
                .map(categoryService::castToCategoryResponseDTO)
                .toList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable long id){
        return new ResponseEntity<>(categoryService.castToCategoryResponseDTO(categoryService.getCategoryById(id)), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<CategoryResponseDTO> saveCategory(@Valid @RequestBody CategoryRequestDTO category){
        return new ResponseEntity<>(categoryService.castToCategoryResponseDTO(categoryService.saveCategory(category)), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryRequestDTO requestDTO){
        Category existing = categoryService.getCategoryById(id);
        existing.setName(requestDTO.name());
        Category updated = categoryService.saveCategory(existing);
        return new ResponseEntity<>(categoryService.castToCategoryResponseDTO(updated), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> patchCategory(@PathVariable long id, @RequestBody String patch){
        Category category = categoryService.getCategoryById(id);

        return new ResponseEntity<>(categoryService.castToCategoryResponseDTO(categoryService.saveCategory(applyPatching(category, patch))), HttpStatus.OK);
    }

    private Category applyPatching(Category target, String patch){
        try{
            JsonNode patchedNode = objectMapper.readTree(patch);
            JsonNode targetNode = objectMapper.convertValue(target, JsonNode.class);

            JsonNode patchValue = JsonPatch.apply(patchedNode,targetNode);

            return objectMapper.treeToValue(patchValue, Category.class);
        } catch (JsonProcessingException e){
            throw new JsonPatchProcessingException("Failed to do patch method on category");
        }
    }
}
