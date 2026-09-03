package com.librarymanagement.LibraryManagement.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for recording a borrowing. Both ids must reference rows that already exist.")
public record BorrowRequestDTO(

        @Schema(description = "Id of the book being borrowed. Must currently be AVAILABLE.", example = "1")
        long bookId,

        @Schema(description = "Id of the customer borrowing the book.", example = "1")
        long customerId) {
}