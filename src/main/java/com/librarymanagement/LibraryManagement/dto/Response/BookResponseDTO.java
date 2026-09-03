package com.librarymanagement.LibraryManagement.dto.Response;

import com.librarymanagement.LibraryManagement.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "A book as returned by the API.")
public record BookResponseDTO(

        @Schema(description = "Generated database id.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        long id,

        @Schema(description = "Book title.", example = "Men in the Sun")
        String title,

        @Schema(description = "ISBN-13 or ISBN-10.", example = "978-0894108570")
        String isbn,

        @Schema(description = "Number of pages.", example = "96")
        int pageCount,

        @Schema(description = "Current shelf status.")
        Status status,

        @Schema(description = "Date the book was added to the catalogue.", accessMode = Schema.AccessMode.READ_ONLY)
        Date addedDate,

        @Schema(description = "Name of the book's author.", example = "Ghassan Kanafani")
        String authorName,

        @Schema(description = "Name of the book's category.", example = "Fiction")
        String categoryName) {
}