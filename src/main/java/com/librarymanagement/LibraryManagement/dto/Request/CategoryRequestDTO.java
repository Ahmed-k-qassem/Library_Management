package com.librarymanagement.LibraryManagement.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for creating or replacing a category.")
public record CategoryRequestDTO(

        @Schema(description = "Category name.", example = "Science Fiction")
        @NotBlank
        String name) {
}