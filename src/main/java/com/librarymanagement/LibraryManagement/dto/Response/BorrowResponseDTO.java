package com.librarymanagement.LibraryManagement.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "A borrowing record as returned by the API.")
public record BorrowResponseDTO(

        @Schema(description = "Generated database id of the borrowing record.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        long borrowId,

        @Schema(description = "Title of the borrowed book.", example = "Men in the Sun")
        String bookTitle,

        @Schema(description = "Name of the customer who borrowed the book.", example = "Mahmoud Abdel Rahman Youssef")
        String customerName,

        @Schema(description = "When the borrowing was recorded.", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime borrowDate) {
}