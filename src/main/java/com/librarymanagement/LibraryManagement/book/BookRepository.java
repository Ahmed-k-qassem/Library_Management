package com.librarymanagement.LibraryManagement.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = """
            select new com.librarymanagement.LibraryManagement.book.BookResponseDTO(
                b.id, b.title, b.isbn, b.pageCount, b.status, b.addedDate,
                a.authorName, c.name)
            from Book b
            left join b.author a
            left join b.category c
            """,
            countQuery = "select count(b) from Book b")
    Page<BookResponseDTO> findAllSummaries(Pageable pageable);

    @Query(value = """
            select new com.librarymanagement.LibraryManagement.book.BookAuthorResponseDTO(
                b.title, b.isbn, b.pageCount, b.status, a.authorName)
            from Book b
            join b.author a
            where a.id = :id
            """,
            countQuery = "select count(b) from Book b where b.author.id = :id")
    Page<BookAuthorResponseDTO> getBooksByAuthorId(@Param("id") long id, Pageable pageable);

    Optional<Book> getBookById(long id);

    @Modifying
    @Query("DELETE FROM Book b WHERE b.id = :id")
    int deleteBookById(@Param("id") long id);
}