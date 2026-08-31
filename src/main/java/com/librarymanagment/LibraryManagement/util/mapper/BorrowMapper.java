package com.librarymanagment.LibraryManagement.util.mapper;

import com.librarymanagment.LibraryManagement.entity.Book;
import com.librarymanagment.LibraryManagement.entity.Borrow;
import com.librarymanagment.LibraryManagement.entity.Customer;
import com.librarymanagment.LibraryManagement.dto.Request.BorrowRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.BorrowResponseDTO;
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
