package com.librarymanagment.LibraryManagement.dto.Response;

import java.time.LocalDateTime;

public record BorrowResponseDTO(long borrowId, String bookTitle, String customerName, LocalDateTime borrowDate){
}
