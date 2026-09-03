package com.librarymanagement.LibraryManagement.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Payload for creating or replacing an author.")
public record AuthorRequestDTO(

        @Schema(
                description = "The author's full name as it should appear in the catalogue.",
                example = "Ghassan Kanafani")
        @NotBlank(message = "please enter author name")
        String authorName,

        @Schema(
                description = "Nationality as a plain word. Letters and spaces only.",
                example = "Palestinian")
        @NotBlank(message = "please select nationality")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Nationality cannot have numbers.")
        String nationality) {
}