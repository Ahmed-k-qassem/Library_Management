package com.librarymanagment.LibraryManagment.dto.Request;

import com.librarymanagment.LibraryManagment.entity.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record  BookRequestDTO(
                            @NotBlank(message = "The title must be entered")
                            @Size(min = 10, message = "Book title must be 10 or above in characters")
                            String title,

                            @NotBlank(message = "You must enter ISBN")
                            String isbn,

                            @NotNull(message = "You must enter a page number")
                            @Positive(message = "Pages cannot be negative")
                            int pageCount,

                            @Enumerated(EnumType.STRING)
                            Status status,

                            @NotNull(message = "Author ID is required")
                            Long authorId,

                            @NotNull(message = "Category ID is required")
                            Long categoryId){

}
