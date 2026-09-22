package com.librarymanagement.LibraryManagement.book;

import com.librarymanagement.LibraryManagement.author.Author;
import com.librarymanagement.LibraryManagement.category.Category;
import org.springframework.stereotype.Component;

@Component
class BookMapper {
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
