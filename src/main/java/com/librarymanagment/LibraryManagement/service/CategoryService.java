package com.librarymanagment.LibraryManagement.service;

import com.librarymanagment.LibraryManagement.entity.Category;
import com.librarymanagment.LibraryManagement.repository.CategoryRepository;
import com.librarymanagment.LibraryManagement.dto.Request.CategoryRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.CategoryResponseDTO;
import com.librarymanagment.LibraryManagement.util.GenericPatcher;
import com.librarymanagment.LibraryManagement.util.mapper.CategoryMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final GenericPatcher genericPatcher;
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, GenericPatcher genericPatcher) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.genericPatcher = genericPatcher;
    }


    public List<CategoryResponseDTO> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::mapCategoryToResponseDTO)
                .toList();
    }


    @Transactional
    public CategoryResponseDTO saveCategory(CategoryRequestDTO category){
        Assert.notNull(category, "Dev error: request is null");
        return categoryMapper.mapCategoryToResponseDTO(categoryRepository.save(categoryMapper.mapRequestDTOtoCategory(category)));
    }

    public Category getCategoryById(long id){
        return categoryRepository.getCategoryById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Transactional
    public CategoryResponseDTO getCategoryResponseById(long id){
        return categoryMapper.mapCategoryToResponseDTO(getCategoryById(id));
    }

    @Transactional
    public void deleteCategory(long id){
        int rowsAffected = categoryRepository.deleteCategoryById(id);
        if(rowsAffected == 0){
            throw new EntityNotFoundException("Category not found - deletion failed");
        }
    }


    @Transactional
    public CategoryResponseDTO updateCategory(long id,CategoryRequestDTO category){
        Category updated = getCategoryById(id);
        updated.setName(category.name());
        return categoryMapper.mapCategoryToResponseDTO(categoryRepository.save(updated));
    }


    @Transactional
    public CategoryResponseDTO patchCategory(long id, String patchBody){
        Category category = getCategoryById(id);
        CategoryResponseDTO dtoToPatch =  categoryMapper.mapCategoryToResponseDTO(category);
        CategoryResponseDTO patchedDTO = genericPatcher.applyPatch(patchBody, dtoToPatch, CategoryResponseDTO.class);
        category.setName(patchedDTO.name());
        return categoryMapper.mapCategoryToResponseDTO(categoryRepository.save(category));
    }


}
