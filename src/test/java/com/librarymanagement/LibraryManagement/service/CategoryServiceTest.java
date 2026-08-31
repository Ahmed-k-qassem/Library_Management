package com.librarymanagement.LibraryManagement.service;

import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.repository.CategoryRepository;
import com.librarymanagement.LibraryManagement.dto.Request.CategoryRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.CategoryResponseDTO;
import com.librarymanagement.LibraryManagement.util.GenericPatcher;
import com.librarymanagement.LibraryManagement.util.dto.request.CategoryRequestDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.dto.response.CategoryResponseDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.entity.CategoryTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private GenericPatcher patcher;

    @InjectMocks
    private CategoryService categoryService;


    @Test
    void saveCategory_shouldReturnResponseDTO(){
        CategoryRequestDTO request = CategoryRequestDtoTestDataBuilder.getInstance().build();
        Category categoryEntity = CategoryTestDataBuilder.getInstance().build();
        CategoryResponseDTO expected = CategoryResponseDtoTestDataBuilder.getInstance().build();

        when(categoryMapper.mapRequestDTOtoCategory(request)).thenReturn(categoryEntity);
        when(categoryMapper.mapCategoryToResponseDTO(categoryEntity)).thenReturn(expected);
        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);

        CategoryResponseDTO actual = categoryService.saveCategory(request);

        assertThat(actual).isEqualTo(expected);

        verify(categoryRepository, times(1)).save(categoryEntity);
        verify(categoryMapper, times(1)).mapCategoryToResponseDTO(categoryEntity);
        verify(categoryMapper, times(1)).mapRequestDTOtoCategory(request);
    }


    @Test
    void saveCategory_shouldThrowException_WhenRequestIsNull(){
        CategoryRequestDTO request= null;

        assertThatThrownBy(() -> categoryService.saveCategory(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dev error: request is null");

        verifyNoInteractions(categoryRepository);
    }


}
