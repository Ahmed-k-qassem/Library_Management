package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.util.AbstractMySqlIntegrationTest;
import com.librarymanagement.LibraryManagement.util.entity.AuthorTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class AuthorRepositoryTest extends AbstractMySqlIntegrationTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    AuthorRepository authorRepository;


    @Test
    @DisplayName("Modifying query: Delete author by id must delete author and return 1")
    void givenValidAuthorId_whenDeleteAuthorById_thenReturn1(){
        Author author = entityManager.persistAndFlush(AuthorTestDataBuilder.anAuthor()
                .withAuthorName("Ahmed").withoutId().withNationality("Syrian").build());

        int rowsAffected = authorRepository.deleteAuthorById(author.getId());
        entityManager.clear();

        assertThat(rowsAffected).isEqualTo(1);
        assertThat(authorRepository.findAuthorById(author.getId())).isEmpty();
    }


    @Test
    @DisplayName("Modifying query: delete author by id must return 0 for invalid id")
    void givenNonValidAuthorId_whenDeleteAuthorById_thenReturn0(){
        int rowsAffected = authorRepository.deleteAuthorById(99L);

        assertThat(rowsAffected).isEqualTo(0);
    }
}
