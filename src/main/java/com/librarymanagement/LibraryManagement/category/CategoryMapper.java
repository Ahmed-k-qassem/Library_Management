package com.librarymanagement.LibraryManagement.category;

import org.springframework.stereotype.Component;

@Component
class CategoryMapper {
    public CategoryResponseDTO mapCategoryToResponseDTO(Category category){
        return new CategoryResponseDTO(category.getId(), category.getName());
    }


    public Category mapRequestDTOtoCategory(CategoryRequestDTO categoryRequestDTO){
        return new Category(categoryRequestDTO.name());
    }
}
