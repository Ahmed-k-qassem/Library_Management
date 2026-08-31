package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import com.librarymanagement.LibraryManagement.entity.Status;

//String bookName, String isbn, int pageCount, Status status, String authorName
public class BookAuthorResponseTestDataBuilder {
    private String title = "default_name";
    private String isbn = "124-282-2847";
    private int pageCount = 100;
    private Status status = Status.AVAILABLE;
    private String authorName = "Ahmed";

    private BookAuthorResponseTestDataBuilder(){

    }

    public static BookAuthorResponseTestDataBuilder getInstance(){
        return new BookAuthorResponseTestDataBuilder();
    }

    public BookAuthorResponseTestDataBuilder withTitle(String title){
        this.title = title;
        return this;
    }

    public BookAuthorResponseTestDataBuilder withIsbn(String isbn){
        this.isbn = isbn;
        return this;
    }

    public BookAuthorResponseTestDataBuilder withPageCount(int pageCount){
        this.pageCount = pageCount;
        return this;
    }

    public BookAuthorResponseTestDataBuilder withStatus(Status status){
        this.status =status;
        return this;
    }

    public BookAuthorResponseTestDataBuilder withAuthorName(String authorName){
        this.authorName = authorName;
        return this;
    }

    public BookAuthorResponseDTO build(){
        return new BookAuthorResponseDTO(title, isbn, pageCount, status,authorName);
    }
}
