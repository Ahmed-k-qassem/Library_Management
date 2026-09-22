package com.librarymanagement.LibraryManagement.borrow;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
