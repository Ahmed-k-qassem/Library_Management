package com.librarymanagement.LibraryManagement.book;

import com.librarymanagement.LibraryManagement.author.Author;
import com.librarymanagement.LibraryManagement.category.Category;
import com.librarymanagement.LibraryManagement.common.AbstractMySqlIntegrationTest;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BookRepositoryTest extends AbstractMySqlIntegrationTest {


    private static final Sort BY_TITLE_THEN_ID = Sort.by("title").and(Sort.by("id"));

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

        Page<BookAuthorResponseDTO> results =
                bookRepository.getBooksByAuthorId(tolkien.getId(), PageRequest.of(0, 20));

        assertThat(results.getTotalElements()).isEqualTo(2);
        assertThat(results.getContent())
                .extracting(BookAuthorResponseDTO::bookName)
                .containsExactlyInAnyOrder("The Hobbit", "The Lord of the Rings");
        assertThat(results.getContent())
                .allMatch(b -> b.authorName().equals("J.R.R. Tolkien"));
    }

    @Test
    @DisplayName("Custom JPQL: getBooksByAuthorId returns an empty page when author has no books")
    void givenAuthorWithNoBooks_whenGetBooksByAuthorId_thenReturnsEmptyPage() {
        Author author = entityManager.persistAndFlush(new Author("No Books Author", "Egyptian"));

        Page<BookAuthorResponseDTO> results =
                bookRepository.getBooksByAuthorId(author.getId(), PageRequest.of(0, 20));

        assertThat(results.getContent()).isEmpty();
        assertThat(results.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Custom JPQL: getBooksByAuthorId excludes books with no author")
    void givenBookWithNoAuthor_whenGetBooksByAuthorId_thenNotReturned() {
        Author author = entityManager.persistAndFlush(new Author("Some Author", "British"));
        entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("Orphan Book").withIsbn("978-4").withAuthor(null).build());

        Page<BookAuthorResponseDTO> results =
                bookRepository.getBooksByAuthorId(author.getId(), PageRequest.of(0, 20));

        assertThat(results.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Custom JPQL: getBooksByAuthorId pages, and its count only counts that author's books")
    void givenAuthorWithFiveBooks_whenPagedByTwo_thenCountScopedToAuthor() {
        Author orwell = entityManager.persist(new Author("George Orwell", "British"));
        Author other = entityManager.persist(new Author("Someone Else", "British"));
        for (int i = 0; i < 5; i++) {
            entityManager.persist(BookTestDataBuilder.aBook()
                    .withTitle("Orwell " + i).withIsbn("o-" + i).withAuthor(orwell).build());
        }
        entityManager.persist(BookTestDataBuilder.aBook()
                .withTitle("Not Orwell").withIsbn("x-1").withAuthor(other).build());
        entityManager.flush();

        Page<BookAuthorResponseDTO> second =
                bookRepository.getBooksByAuthorId(orwell.getId(), PageRequest.of(1, 2, BY_TITLE_THEN_ID));

        assertThat(second.getContent()).hasSize(2);
        assertThat(second.getTotalElements()).isEqualTo(5);
        assertThat(second.getTotalPages()).isEqualTo(3);
    }


    @Test
    @DisplayName("Paging: first page full, last page partial, page past the end empty")
    void givenTwentyFiveBooks_whenPaging_thenBoundariesAndCountsCorrect() {
        seedBooks(25);

        Page<BookResponseDTO> first = bookRepository.findAllSummaries(PageRequest.of(0, 10, BY_TITLE_THEN_ID));
        Page<BookResponseDTO> last = bookRepository.findAllSummaries(PageRequest.of(2, 10, BY_TITLE_THEN_ID));
        Page<BookResponseDTO> past = bookRepository.findAllSummaries(PageRequest.of(9, 10, BY_TITLE_THEN_ID));

        assertThat(first.getContent()).hasSize(10);
        assertThat(first.getTotalElements()).isEqualTo(25);
        assertThat(first.getTotalPages()).isEqualTo(3);
        assertThat(first.isFirst()).isTrue();
        assertThat(first.isLast()).isFalse();

        assertThat(last.getNumberOfElements()).isEqualTo(5);
        assertThat(last.getSize()).isEqualTo(10);
        assertThat(last.isLast()).isTrue();

        assertThat(past.getContent()).isEmpty();
        assertThat(past.getTotalElements()).isEqualTo(25);
    }

    @Test
    @DisplayName("Paging: walking every page sees every row exactly once")
    void givenTwentyFiveBooks_whenAllPagesWalked_thenNoDuplicatesAndNoGaps() {
        seedBooks(25);

        List<Long> seen = new ArrayList<>();
        Page<BookResponseDTO> page = bookRepository.findAllSummaries(PageRequest.of(0, 10, BY_TITLE_THEN_ID));
        seen.addAll(page.map(BookResponseDTO::id).getContent());
        while (page.hasNext()) {
            page = bookRepository.findAllSummaries(page.nextPageable());
            seen.addAll(page.map(BookResponseDTO::id).getContent());
        }

        assertThat(seen).hasSize(25).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Paging: sorting applies to the whole table before slicing")
    void givenBooksInsertedOutOfOrder_whenFirstPage_thenAlphabeticallyFirstRows() {
        entityManager.persist(BookTestDataBuilder.aBook().withTitle("Zorba").withIsbn("z").build());
        entityManager.persist(BookTestDataBuilder.aBook().withTitle("Middlemarch").withIsbn("m").build());
        entityManager.persist(BookTestDataBuilder.aBook().withTitle("Aeneid").withIsbn("a").build());
        entityManager.flush();

        Page<BookResponseDTO> page = bookRepository.findAllSummaries(PageRequest.of(0, 2, BY_TITLE_THEN_ID));

        assertThat(page.getContent())
                .extracting(BookResponseDTO::title)
                .containsExactly("Aeneid", "Middlemarch");
    }

    @Test
    @DisplayName("Paging: duplicate titles split across a page boundary come back as two different rows, in id order")
    void givenDuplicateTitles_whenPagedBySizeOne_thenTiebreakerKeepsOrderStable() {
        entityManager.persist(BookTestDataBuilder.aBook().withTitle("Dune").withIsbn("dune-a").build());
        entityManager.persist(BookTestDataBuilder.aBook().withTitle("Dune").withIsbn("dune-b").build());
        entityManager.flush();

        long firstPageId = bookRepository.findAllSummaries(PageRequest.of(0, 1, BY_TITLE_THEN_ID))
                .getContent().get(0).id();
        long secondPageId = bookRepository.findAllSummaries(PageRequest.of(1, 1, BY_TITLE_THEN_ID))
                .getContent().get(0).id();


        assertThat(firstPageId).isLessThan(secondPageId);
    }

    @Test
    @DisplayName("Projection: a book with no author or category is still listed (left join)")
    void givenBookWithoutAuthorOrCategory_whenFindAllSummaries_thenStillListed() {
        entityManager.persistAndFlush(BookTestDataBuilder.aBook().withTitle("Orphan").withIsbn("orph").build());

        Page<BookResponseDTO> page = bookRepository.findAllSummaries(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        BookResponseDTO row = page.getContent().get(0);
        assertThat(row.title()).isEqualTo("Orphan");
        assertThat(row.authorName()).isNull();
        assertThat(row.categoryName()).isNull();
    }

    @Test
    @DisplayName("Projection: each column lands in the right record component")
    void givenKnownBook_whenFindAllSummaries_thenFieldsLandInCorrectPositions() {
        Author orwell = entityManager.persist(new Author("George Orwell", "British"));
        Category dystopian = entityManager.persist(new Category("Dystopian"));
        Book book = entityManager.persistAndFlush(BookTestDataBuilder.aBook()
                .withTitle("1984").withIsbn("978-0451524935").withPageCount(328)
                .withAuthor(orwell).withCategory(dystopian).build());

        BookResponseDTO row = bookRepository.findAllSummaries(PageRequest.of(0, 10))
                .getContent().get(0);


        assertThat(row.id()).isEqualTo(book.getId());
        assertThat(row.title()).isEqualTo("1984");
        assertThat(row.isbn()).isEqualTo("978-0451524935");
        assertThat(row.pageCount()).isEqualTo(328);
        assertThat(row.authorName()).isEqualTo("George Orwell");
        assertThat(row.categoryName()).isEqualTo("Dystopian");
    }


    @Test
    @DisplayName("Query count: one page with authors and categories costs exactly two statements")
    void givenBooksWithAuthorsAndCategories_whenFindAllSummaries_thenTwoStatements() {
        for (int i = 0; i < 10; i++) {
            Author author = entityManager.persist(new Author("Author " + i, "British"));
            Category category = entityManager.persist(new Category("Category " + i));
            entityManager.persist(BookTestDataBuilder.aBook()
                    .withTitle("Book " + i).withIsbn("q-" + i)
                    .withAuthor(author).withCategory(category).build());
        }
        entityManager.flush();
        entityManager.clear();

        Statistics stats = entityManager.getEntityManager()
                .getEntityManagerFactory()
                .unwrap(SessionFactory.class)
                .getStatistics();
        stats.clear();

        Page<BookResponseDTO> page = bookRepository.findAllSummaries(PageRequest.of(0, 5, BY_TITLE_THEN_ID));

        assertThat(page.getContent()).hasSize(5);
        assertThat(stats.getPrepareStatementCount()).isEqualTo(2);
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

    private void seedBooks(int count) {
        for (int i = 0; i < count; i++) {
            entityManager.persist(BookTestDataBuilder.aBook()
                    .withTitle(String.format("Book %02d", i))
                    .withIsbn("isbn-" + i)
                    .build());
        }
        entityManager.flush();
    }
}