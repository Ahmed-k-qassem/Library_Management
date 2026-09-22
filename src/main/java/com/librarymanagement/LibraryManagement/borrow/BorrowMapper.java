package com.librarymanagement.LibraryManagement.borrow;

import com.librarymanagement.LibraryManagement.book.Book;
import com.librarymanagement.LibraryManagement.customer.Customer;
import org.springframework.stereotype.Component;

@Component
class BorrowMapper {
    public BorrowResponseDTO mapBorrowToBorrowResponseDTO(Borrow borrow) {
        return new BorrowResponseDTO(borrow.getId(), borrow.getBook().getTitle(), borrow.getCustomer().getName(), borrow.getBorrowDate());
    }

    public Borrow mapBorrowRequestDTOtoBorrow(BorrowRequestDTO borrowRequestDTO, Book book, Customer customer) {
        return new Borrow(book, customer);
    }
}
