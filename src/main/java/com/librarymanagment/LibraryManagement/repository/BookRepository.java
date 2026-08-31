package com.librarymanagment.LibraryManagement.repository;

import com.librarymanagment.LibraryManagement.entity.Book;
import com.librarymanagment.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT new com.librarymanagment.LibraryManagement.dto.Response.BookAuthorResponseDTO(b.title, b.isbn, b.pageCount, b.status, a.authorName) " +
            "FROM Book b JOIN b.author a " +
            "WHERE a.id = :id")
    List<BookAuthorResponseDTO> getBooksByAuthorId(@Param("id") long id);


    Optional<Book> getBookById(long id);


    @Modifying
    @Query("DELETE FROM Book b WHERE b.id = :id")
    int deleteBookById(@Param("id") long id);


}
