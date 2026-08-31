package com.librarymanagment.LibraryManagement.util.mapper;

import com.librarymanagment.LibraryManagement.entity.Author;
import com.librarymanagment.LibraryManagement.entity.Book;
import com.librarymanagment.LibraryManagement.entity.Category;
import com.librarymanagment.LibraryManagement.dto.Request.BookRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.BookResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookResponseDTO mapBookToResponseDTO(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPageCount(),
                book.getStatus(),
                book.getAddedDate(),
                book.getAuthor().getAuthorName(),
                book.getCategory().getName()
        );
    }


    public Book mapRequestDTOtoBook(BookRequestDTO dto, Author author, Category category) {
        Book newBook = new Book();
        newBook.setTitle(dto.title());
        newBook.setIsbn(dto.isbn());
        newBook.setPageCount(dto.pageCount());
        newBook.setStatus(dto.status());
        newBook.setAuthor(author);
        newBook.setCategory(category);
        return newBook;
    }
}
