package com.librarymanagement.LibraryManagement.util.dto.request;

import com.librarymanagement.LibraryManagement.dto.Request.BorrowRequestDTO;

public class BorrowRequestDtoTestDataBuilder {
    private Long customerId = 1L;
    private Long bookId = 1L;

    private BorrowRequestDtoTestDataBuilder(){

    }

    public static BorrowRequestDtoTestDataBuilder getInstance(){
        return new BorrowRequestDtoTestDataBuilder();
    }

    public BorrowRequestDtoTestDataBuilder withCustomerId(Long id){
        this.customerId = id;
        return this;
    }

    public BorrowRequestDtoTestDataBuilder withBookId(Long id){
        this.bookId = id;
        return this;
    }

    public BorrowRequestDTO build(){
        return new BorrowRequestDTO(bookId, customerId);
    }
}
