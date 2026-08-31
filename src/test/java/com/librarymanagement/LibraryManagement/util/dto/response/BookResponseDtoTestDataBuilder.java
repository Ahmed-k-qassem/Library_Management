package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.BookResponseDTO;
import com.librarymanagement.LibraryManagement.entity.Status;

import java.util.Date;

public class BookResponseDtoTestDataBuilder {

    private long id = 1L;
    private String title = "Effective Java Third Edition";
    private String isbn = "978-0134685991";
    private int pageCount = 412;
    private Status status = Status.AVAILABLE;
    private Date addedDate = new Date();
    private String authorName = "Joshua Bloch";
    private String categoryName = "Programming";

    private BookResponseDtoTestDataBuilder() {
    }

    public static BookResponseDtoTestDataBuilder getInstance() {
        return new BookResponseDtoTestDataBuilder();
    }

    public BookResponseDtoTestDataBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public BookResponseDtoTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookResponseDtoTestDataBuilder withAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public BookResponseDtoTestDataBuilder withCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public BookResponseDTO build() {
        return new BookResponseDTO(id, title, isbn, pageCount, status, addedDate, authorName, categoryName);
    }
}
