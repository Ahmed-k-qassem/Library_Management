package com.librarymanagement.LibraryManagement.util.mapper;

import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.dto.Request.CategoryRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.CategoryResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDTO mapCategoryToResponseDTO(Category category){
        return new CategoryResponseDTO(category.getId(), category.getName());
    }


    public Category mapRequestDTOtoCategory(CategoryRequestDTO categoryRequestDTO){
        return new Category(categoryRequestDTO.name());
    }
}
