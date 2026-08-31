package com.librarymanagement.LibraryManagement.util.mapper;

import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.entity.Book;
import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.dto.Request.BookRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BookResponseDTO;
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
