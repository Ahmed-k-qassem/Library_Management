package com.librarymanagment.LibraryManagment.util.mapper;

import com.librarymanagment.LibraryManagment.Entities.Book;
import com.librarymanagment.LibraryManagment.Entities.Borrow;
import com.librarymanagment.LibraryManagment.Entities.Customer;
import com.librarymanagment.LibraryManagment.dto.Request.BorrowRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.BorrowResponseDTO;
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
