package com.librarymanagment.LibraryManagment.service;

import com.librarymanagment.LibraryManagment.entity.Book;
import com.librarymanagment.LibraryManagment.entity.Borrow;
import com.librarymanagment.LibraryManagment.entity.Customer;
import com.librarymanagment.LibraryManagment.entity.Status;
import com.librarymanagment.LibraryManagment.repository.BorrowRepository;
import com.librarymanagment.LibraryManagment.dto.Request.BorrowRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.BorrowResponseDTO;
import com.librarymanagment.LibraryManagment.util.mapper.BorrowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BorrowService {
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


    @Transactional
    public List<BorrowResponseDTO> getAllBorrows() {
        return borrowRepository.findAll()
                .stream()
                .map(borrowMapper::mapBorrowToBorrowResponseDTO)
                .toList();
    }
}
