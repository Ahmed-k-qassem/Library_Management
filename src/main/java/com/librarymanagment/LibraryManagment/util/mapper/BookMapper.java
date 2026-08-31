package com.librarymanagment.LibraryManagment.util.mapper;

import com.librarymanagment.LibraryManagment.entity.Author;
import com.librarymanagment.LibraryManagment.entity.Book;
import com.librarymanagment.LibraryManagment.entity.Category;
import com.librarymanagment.LibraryManagment.dto.Request.BookRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.BookResponseDTO;
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
