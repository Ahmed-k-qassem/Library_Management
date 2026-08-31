package com.librarymanagment.LibraryManagement.util.mapper;

import com.librarymanagment.LibraryManagement.entity.Category;
import com.librarymanagment.LibraryManagement.dto.Request.CategoryRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.CategoryResponseDTO;
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
