package com.librarymanagment.LibraryManagment.util.mapper;

import com.librarymanagment.LibraryManagment.entity.Category;
import com.librarymanagment.LibraryManagment.dto.Request.CategoryRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.CategoryResponseDTO;
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
