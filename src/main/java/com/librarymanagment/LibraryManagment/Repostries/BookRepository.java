package com.librarymanagment.LibraryManagment.Repostries;

import com.librarymanagment.LibraryManagment.Entities.Book;
import com.librarymanagment.LibraryManagment.dto.Response.BookAuthorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT new com.librarymanagment.LibraryManagment.dto.Response.BookAuthorDTO(b.title, b.isbn, b.pageCount, b.status, a.authorName) " +
            "FROM Book b JOIN b.author a " +
            "WHERE a.id = :id")
    List<BookAuthorDTO> getBooksByAuthorId(@Param("id") long id);


    Optional<Book> getBookById(long id);


    @Modifying
    @Query("DELETE FROM Book b WHERE b.id = :id")
    int deleteBookById(@Param("id") long id);


}
