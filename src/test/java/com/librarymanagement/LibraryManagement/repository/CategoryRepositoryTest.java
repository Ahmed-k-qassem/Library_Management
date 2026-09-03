package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.util.AbstractMySqlIntegrationTest;
import com.librarymanagement.LibraryManagement.util.entity.CategoryTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest extends AbstractMySqlIntegrationTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("Modifying query: delete category by id must delete category and return 1")
    void givenValidCategoryId_whenDeleteCategoryById_thenReturn1() {
        Category category = entityManager.persistAndFlush(CategoryTestDataBuilder.getInstance()
                .withoutId().withName("Programming").build());

        int rowsAffected = categoryRepository.deleteCategoryById(category.getId());
        entityManager.clear();

        assertThat(rowsAffected).isEqualTo(1);
        assertThat(categoryRepository.getCategoryById(category.getId())).isEmpty();
    }

    @Test
    @DisplayName("Modifying query: delete category by id must return 0 for invalid id")
    void givenNonValidCategoryId_whenDeleteCategoryById_thenReturn0() {
        int rowsAffected = categoryRepository.deleteCategoryById(99L);

        assertThat(rowsAffected).isEqualTo(0);
    }
}