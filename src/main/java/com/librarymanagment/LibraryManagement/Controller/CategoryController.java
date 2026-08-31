package com.librarymanagment.LibraryManagement.Controller;

import com.librarymanagment.LibraryManagement.service.CategoryService;
import com.librarymanagment.LibraryManagement.dto.Request.CategoryRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.CategoryResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {


    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponseDTO> getCategoryList(){
        return categoryService.getAllCategories();
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable long id){
        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryResponseById(id);
        return new ResponseEntity<>(categoryResponseDTO, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<CategoryResponseDTO> saveCategory(@Valid @RequestBody CategoryRequestDTO category){
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryRequestDTO requestDTO){
        return new ResponseEntity<>(categoryService.updateCategory(id,requestDTO), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<CategoryResponseDTO> patchCategory(@PathVariable long id, @RequestBody String patch){
        return new ResponseEntity<>(categoryService.patchCategory(id,patch), HttpStatus.OK);
    }

}
