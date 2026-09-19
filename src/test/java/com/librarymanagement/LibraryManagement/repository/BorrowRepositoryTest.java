package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.entity.Book;
import com.librarymanagement.LibraryManagement.entity.Borrow;
import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.entity.Customer;
import com.librarymanagement.LibraryManagement.entity.User;
import com.librarymanagement.LibraryManagement.util.AbstractMySqlIntegrationTest;
import com.librarymanagement.LibraryManagement.util.entity.BookTestDataBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BorrowRepositoryTest extends AbstractMySqlIntegrationTest {

    private static final Sort NEWEST_FIRST =
            Sort.by(Sort.Direction.DESC, "borrowDate").and(Sort.by(Sort.Direction.DESC, "id"));

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 15, 10, 0, 0);

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BorrowRepository borrowRepository;

    private Book dune;
    private Customer layla;

    @BeforeEach
    void seedBookAndCustomer() {
        dune = entityManager.persist(BookTestDataBuilder.aBook().withTitle("Dune").withIsbn("978-dune").build());
        layla = entityManager.persist(new Customer("Layla Haddad", null));
        entityManager.flush();
    }


    @Test
    @DisplayName("Paging: first page full, last page partial, page past the end empty")
    void givenTwentyFiveBorrows_whenPaging_thenBoundariesAndCountsCorrect() {
        seedBorrows(25);

        Page<BorrowResponseDTO> first = borrowRepository.findAllSummaries(PageRequest.of(0, 10, NEWEST_FIRST));
        Page<BorrowResponseDTO> last = borrowRepository.findAllSummaries(PageRequest.of(2, 10, NEWEST_FIRST));
        Page<BorrowResponseDTO> past = borrowRepository.findAllSummaries(PageRequest.of(9, 10, NEWEST_FIRST));

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
    @DisplayName("Paging: walking every page sees every borrowing exactly once")
    void givenTwentyFiveBorrows_whenAllPagesWalked_thenNoDuplicatesAndNoGaps() {
        seedBorrows(25);

        List<Long> seen = new ArrayList<>();
        Page<BorrowResponseDTO> page = borrowRepository.findAllSummaries(PageRequest.of(0, 10, NEWEST_FIRST));
        seen.addAll(page.map(BorrowResponseDTO::borrowId).getContent());
        while (page.hasNext()) {
            page = borrowRepository.findAllSummaries(page.nextPageable());
            seen.addAll(page.map(BorrowResponseDTO::borrowId).getContent());
        }

        assertThat(seen).hasSize(25).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Sorting: newest first across the whole table, regardless of insert order")
    void givenBorrowsInsertedOutOfOrder_whenNewestFirst_thenLatestDatesOnFirstPage() {
        persistBorrow(dune, layla, BASE.plusDays(1));
        persistBorrow(dune, layla, BASE.plusDays(30));
        persistBorrow(dune, layla, BASE.plusDays(10));
        entityManager.flush();

        Page<BorrowResponseDTO> page = borrowRepository.findAllSummaries(PageRequest.of(0, 2, NEWEST_FIRST));

        assertThat(page.getContent())
                .extracting(BorrowResponseDTO::borrowDate)
                .containsExactly(BASE.plusDays(30), BASE.plusDays(10));
    }

    @Test
    @DisplayName("Sorting: borrowings sharing a timestamp are split across pages as distinct rows, id DESC")
    void givenSameTimestamp_whenPagedBySizeOne_thenDistinctRowsInIdDescendingOrder() {
        persistBorrow(dune, layla, BASE);
        persistBorrow(dune, layla, BASE);
        persistBorrow(dune, layla, BASE);
        entityManager.flush();

        long p0 = borrowRepository.findAllSummaries(PageRequest.of(0, 1, NEWEST_FIRST)).getContent().get(0).borrowId();
        long p1 = borrowRepository.findAllSummaries(PageRequest.of(1, 1, NEWEST_FIRST)).getContent().get(0).borrowId();
        long p2 = borrowRepository.findAllSummaries(PageRequest.of(2, 1, NEWEST_FIRST)).getContent().get(0).borrowId();

        assertThat(p0).isGreaterThan(p1);
        assertThat(p1).isGreaterThan(p2);
    }


    @Test
    @DisplayName("Projection: each column lands in the right record component")
    void givenKnownBorrow_whenFindAllSummaries_thenFieldsLandInCorrectPositions() {
        Borrow borrow = persistBorrow(dune, layla, BASE);
        entityManager.flush();

        BorrowResponseDTO row = borrowRepository.findAllSummaries(PageRequest.of(0, 10)).getContent().get(0);
        assertThat(row.borrowId()).isEqualTo(borrow.getId());
        assertThat(row.bookTitle()).isEqualTo("Dune");
        assertThat(row.customerName()).isEqualTo("Layla Haddad");
        assertThat(row.borrowDate()).isEqualTo(BASE);
    }

    @Test
    @DisplayName("Projection: a borrowing whose book and customer are missing is still listed (left join)")
    void givenBorrowWithoutBookOrCustomer_whenFindAllSummaries_thenStillListedWithNullNames() {
        persistBorrow(null, null, BASE);
        entityManager.flush();

        Page<BorrowResponseDTO> page = borrowRepository.findAllSummaries(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).bookTitle()).isNull();
        assertThat(page.getContent().get(0).customerName()).isNull();
    }


    @Test
    @DisplayName("Query count: a page of borrowings costs exactly two statements, whole graph populated")
    void givenBorrowsWithFullAssociationGraph_whenFindAllSummaries_thenTwoStatements() {

        for (int i = 0; i < 10; i++) {
            Author author = entityManager.persist(new Author("Author " + i, "British"));
            Category category = entityManager.persist(new Category("Category " + i));
            Book book = entityManager.persist(BookTestDataBuilder.aBook()
                    .withTitle("Book " + i).withIsbn("q-" + i)
                    .withAuthor(author).withCategory(category).build());
            User user = entityManager.persist(new User("reader" + i, "kc-" + i, "USER"));
            Customer customer = entityManager.persist(new Customer("Customer " + i, user));
            persistBorrow(book, customer, BASE.plusHours(i));
        }
        entityManager.flush();
        entityManager.clear();
        Statistics stats = entityManager.getEntityManager()
                .getEntityManagerFactory()
                .unwrap(SessionFactory.class)
                .getStatistics();
        stats.clear();

        Page<BorrowResponseDTO> page = borrowRepository.findAllSummaries(PageRequest.of(0, 5, NEWEST_FIRST));

        assertThat(page.getContent()).hasSize(5);
        assertThat(stats.getPrepareStatementCount()).isEqualTo(2);
    }

    private Borrow persistBorrow(Book book, Customer customer, LocalDateTime borrowDate) {
        Borrow borrow = new Borrow(book, customer);
        borrow.setBorrowDate(borrowDate);
        return entityManager.persist(borrow);
    }

    private void seedBorrows(int count) {
        for (int i = 0; i < count; i++) {
            persistBorrow(dune, layla, BASE.plusMinutes(i));
        }
        entityManager.flush();
    }
}