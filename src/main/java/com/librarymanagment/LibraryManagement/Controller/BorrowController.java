package com.librarymanagment.LibraryManagement.Controller;

import com.librarymanagment.LibraryManagement.service.BorrowService;
import com.librarymanagment.LibraryManagement.dto.Request.BorrowRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.BorrowResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    public List<BorrowResponseDTO> getAllBorrowings(){
        return borrowService.getAllBorrows();
    }

    @PostMapping
    public ResponseEntity<BorrowResponseDTO> createBorrow(@RequestBody BorrowRequestDTO borrowRequestDTO){
        return new ResponseEntity<>(borrowService.borrowBook(borrowRequestDTO), HttpStatus.CREATED);
    }
}
