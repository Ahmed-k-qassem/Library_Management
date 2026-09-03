package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.entity.Book;
import com.librarymanagement.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import com.librarymanagement.LibraryManagement.util.AbstractMySqlIntegrationTest;
import com.librarymanagement.LibraryManagement.util.entity.BookTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest extends AbstractMySqlIntegrationTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Custom JPQL: getBooksByAuthorId returns only that author's books")
    void givenBooksByTwoAuthors_whenGetBooksByAuthorId_thenReturnsOnlyThatAuthorsBooks() {
        Author tolkien = entityManager.persistAndFlush(new Author("J.R.R. Tolkien", "British"));
        Author bloch = entityManager.persistAndFlush(new Author("Joshua Bloch", "American"));

        entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("The Hobbit").withIsbn("978-1").withAuthor(tolkien).build());
        entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("The Lord of the Rings").withIsbn("978-2").withAuthor(tolkien).build());
        entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("Effective Java").withIsbn("978-3").withAuthor(bloch).build());

        List<BookAuthorResponseDTO> results = bookRepository.getBooksByAuthorId(tolkien.getId());

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(BookAuthorResponseDTO::bookName)
                .containsExactlyInAnyOrder("The Hobbit", "The Lord of the Rings");
        assertThat(results)
                .allMatch(b -> b.authorName().equals("J.R.R. Tolkien"));
    }

    @Test
    @DisplayName("Custom JPQL: getBooksByAuthorId returns empty list when author has no books")
    void givenAuthorWithNoBooks_whenGetBooksByAuthorId_thenReturnsEmptyList() {
        Author author = entityManager.persistAndFlush(new Author("No Books Author", "Egyptian"));

        List<BookAuthorResponseDTO> results = bookRepository.getBooksByAuthorId(author.getId());

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Custom JPQL: getBooksByAuthorId excludes books with no author")
    void givenBookWithNoAuthor_whenGetBooksByAuthorId_thenNotReturned() {
        Author author = entityManager.persistAndFlush(new Author("Some Author", "British"));
        entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("Orphan Book").withIsbn("978-4").withAuthor(null).build());

        List<BookAuthorResponseDTO> results = bookRepository.getBooksByAuthorId(author.getId());

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Modifying query: deleteBookById removes the row and returns 1")
    void givenExistingBook_whenDeleteBookById_thenRowRemovedAndCountReturned() {
        Book book = entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("Clean Code").withIsbn("978-9").build());

        int rowsAffected = bookRepository.deleteBookById(book.getId());
        entityManager.clear();

        assertThat(rowsAffected).isEqualTo(1);
        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    @Test
    @DisplayName("Modifying query: deleteBookById returns 0 for a missing id")
    void givenNonExistentBookId_whenDeleteBookById_thenZeroRowsAffected() {
        int rowsAffected = bookRepository.deleteBookById(999L);

        assertThat(rowsAffected).isEqualTo(0);
    }
}