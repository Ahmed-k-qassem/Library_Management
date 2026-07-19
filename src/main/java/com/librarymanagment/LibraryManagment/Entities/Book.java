package com.librarymanagment.LibraryManagment.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 10, message = "book title must be 10 or above in characters")
    @NotBlank(message = "the title must be entered")
    private String title;
    
    
    @NotBlank(message = "You must enter ISBN")
    private String isbn;
    
    @NotNull(message = "you must enter a page number")
    @Positive(message = "pages cannot be negative")
    private int pageCount;


    @NotBlank(message = "please select a status")
    private String status;


    private Date addedDate = new Date();

    @ManyToOne
    private Author author;
    @ManyToOne
    private Category category;


    public Book() {
        title = null;
        isbn = null;
        pageCount = 0;
    }

    public Book(String title, String isbn, int pageCount) {
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
    }

    public Book(long id, String title, String isbn, int pageCount, String status, Date addedDate, Author author, Category category) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
