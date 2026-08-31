package com.librarymanagement.LibraryManagement.dto.Request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthorRequestDTO(
                        @NotBlank(message = "please enter author name")
                         String authorName,
                        @NotBlank(message = "please select nationality")
                        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Nationality cannot have numbers.")
                         String nationality) {
}
