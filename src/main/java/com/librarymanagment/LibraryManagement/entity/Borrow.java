package com.librarymanagment.LibraryManagement.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Book book;


    @ManyToOne
    private Customer customer;

    private LocalDateTime borrowDate;

    public Borrow(){
        this.borrowDate = null;
        this.book = null;
        this.customer = null;
    }


    public Borrow(Book book, Customer customer){
        this.book = book;
        this.customer = customer;
        this.borrowDate = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }
}
