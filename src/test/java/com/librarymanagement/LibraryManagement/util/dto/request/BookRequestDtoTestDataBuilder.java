package com.librarymanagement.LibraryManagement.util.dto.request;

import com.librarymanagement.LibraryManagement.entity.Status;
import com.librarymanagement.LibraryManagement.dto.Request.BookRequestDTO;

public class BookRequestDtoTestDataBuilder {

    private String title = "Effective Java Third Edition";
    private String isbn = "978-0134685991";
    private int pageCount = 412;
    private Status status = Status.AVAILABLE;
    private Long authorId = 1L;
    private Long categoryId = 2L;

    private BookRequestDtoTestDataBuilder() {
    }

    public static BookRequestDtoTestDataBuilder getInstance() {
        return new BookRequestDtoTestDataBuilder();
    }

    public BookRequestDtoTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookRequestDtoTestDataBuilder withIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public BookRequestDtoTestDataBuilder withPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public BookRequestDtoTestDataBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public BookRequestDtoTestDataBuilder withAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public BookRequestDtoTestDataBuilder withCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public BookRequestDTO build() {
        return new BookRequestDTO(title, isbn, pageCount, status, authorId, categoryId);
    }
}