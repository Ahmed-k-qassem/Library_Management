package com.librarymanagement.LibraryManagement.dto.Response;

import com.librarymanagement.LibraryManagement.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A book projection used when listing an author's books. Slimmer than BookResponseDTO: no id, no category, no added date.")
public record BookAuthorResponseDTO(

        @Schema(description = "Book title.", example = "Men in the Sun")
        String bookName,

        @Schema(description = "ISBN-13 or ISBN-10.", example = "978-0894108570")
        String isbn,

        @Schema(description = "Number of pages.", example = "96")
        int pageCount,

        @Schema(description = "Current shelf status.")
        Status status,

        @Schema(description = "Name of the book's author.", example = "Ghassan Kanafani")
        String authorName) {
}