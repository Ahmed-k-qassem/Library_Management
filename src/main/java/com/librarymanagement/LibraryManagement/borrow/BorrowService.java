package com.librarymanagement.LibraryManagement.borrow;

import com.librarymanagement.LibraryManagement.book.Book;
import com.librarymanagement.LibraryManagement.book.BookService;
import com.librarymanagement.LibraryManagement.book.Status;
import com.librarymanagement.LibraryManagement.customer.CustomerService;
import com.librarymanagement.LibraryManagement.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class BorrowService {
    private final BorrowRepository borrowRepository;
    private final CustomerService customerService;
    private final BookService bookService;
    private final BorrowMapper borrowMapper;

    public BorrowService(BorrowRepository borrowRepository, CustomerService customerService, BookService bookService, BorrowMapper borrowMapper) {
        this.borrowRepository = borrowRepository;
        this.customerService = customerService;
        this.bookService = bookService;
        this.borrowMapper = borrowMapper;
    }



    @Transactional
    public BorrowResponseDTO borrowBook(BorrowRequestDTO requestDTO) {
        Book book = bookService.updateStatus(bookService.getBookById(requestDTO.bookId()), Status.BORROWED);
        Customer customer = customerService.getCustomerById(requestDTO.customerId());
        Borrow borrow = borrowMapper.mapBorrowRequestDTOtoBorrow(requestDTO,  book, customer);
        return borrowMapper.mapBorrowToBorrowResponseDTO(borrowRepository.save(borrow));
    }


    @Transactional(readOnly = true)
    public Page<BorrowResponseDTO> getAllBorrows(Pageable pageable) {
        return borrowRepository.findAllSummaries(pageable);
    }
}