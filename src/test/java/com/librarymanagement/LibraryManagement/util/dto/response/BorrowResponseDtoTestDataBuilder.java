package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;

import java.time.LocalDateTime;
import java.util.Date;


public class BorrowResponseDtoTestDataBuilder {
    private Long borrowId = 1L;
    private String bookTitle = "default_title";
    private String customerName = "default_name";
    private LocalDateTime localDateTime = LocalDateTime.now();

    private BorrowResponseDtoTestDataBuilder(){

    }

    public static BorrowResponseDtoTestDataBuilder getInstance(){
        return new BorrowResponseDtoTestDataBuilder();
    }

    public BorrowResponseDtoTestDataBuilder withBorrowId(Long id){
        this.borrowId = id;
        return this;
    }

    public BorrowResponseDtoTestDataBuilder withBookTitle(String title){
        this.bookTitle = title;
        return this;
    }

    public BorrowResponseDtoTestDataBuilder withCustomerName(String name){
        this.customerName = name;
        return this;
    }

    public BorrowResponseDtoTestDataBuilder withLocalDateTime(LocalDateTime date){
        this.localDateTime = date;
        return this;
    }

    public BorrowResponseDTO build(){
        return new BorrowResponseDTO(borrowId, bookTitle, customerName, localDateTime);
    }
}
