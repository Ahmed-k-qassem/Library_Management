package com.librarymanagement.LibraryManagement.dto.Request;

import com.librarymanagement.LibraryManagement.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for adding a book to the catalogue.")
public record BookRequestDTO(

        @Schema(
                description = "Book title. Must be at least 10 characters.",
                example = "Men in the Sun")
        @NotBlank(message = "The title must be entered")
        @Size(min = 10, message = "Book title must be 10 or above in characters")
        String title,

        @Schema(
                description = "ISBN-13 or ISBN-10. Stored as given; no format check is applied.",
                example = "978-0894108570")
        @NotBlank(message = "You must enter ISBN")
        String isbn,

        @Schema(description = "Number of pages.", example = "96")
        @NotNull(message = "You must enter a page number")
        @Positive(message = "Pages cannot be negative")
        int pageCount,

        @Schema(description = "Shelf status. Defaults to AVAILABLE when omitted.")
        @Enumerated(EnumType.STRING)
        Status status,

        @Schema(description = "Id of an author that already exists.", example = "1")
        @NotNull(message = "Author ID is required")
        Long authorId,

        @Schema(description = "Id of a category that already exists.", example = "3")
        @NotNull(message = "Category ID is required")
        Long categoryId) {
}