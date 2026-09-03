package com.librarymanagement.LibraryManagement.util.entity;

import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.entity.Book;
import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.entity.Status;

public class BookTestDataBuilder {

    private String title = "default book title";
    private String isbn = "978-0000000000";
    private int pageCount = 300;
    private Status status = Status.AVAILABLE;
    private Author author = null;
    private Category category = null;

    public static BookTestDataBuilder aBook() {
        return new BookTestDataBuilder();
    }

    public BookTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookTestDataBuilder withIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public BookTestDataBuilder withPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public BookTestDataBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public BookTestDataBuilder withAuthor(Author author) {
        this.author = author;
        return this;
    }

    public BookTestDataBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public Book build() {
        Book book = new Book(title, isbn, pageCount);
        book.setStatus(status);
        book.setAuthor(author);
        book.setCategory(category);
        return book;
    }
}