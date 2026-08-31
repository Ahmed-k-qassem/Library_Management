package com.librarymanagement.LibraryManagement.util.mapper;

import com.librarymanagement.LibraryManagement.entity.Book;
import com.librarymanagement.LibraryManagement.entity.Borrow;
import com.librarymanagement.LibraryManagement.entity.Customer;
import com.librarymanagement.LibraryManagement.dto.Request.BorrowRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class BorrowMapper {
    public BorrowResponseDTO mapBorrowToBorrowResponseDTO(Borrow borrow) {
        return new BorrowResponseDTO(borrow.getId(), borrow.getBook().getTitle(), borrow.getCustomer().getName(), borrow.getBorrowDate());
    }

    public Borrow mapBorrowRequestDTOtoBorrow(BorrowRequestDTO borrowRequestDTO, Book book, Customer customer) {
        return new Borrow(book, customer);
    }
}
