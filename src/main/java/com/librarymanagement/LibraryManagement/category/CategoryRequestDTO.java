package com.librarymanagement.LibraryManagement.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for creating or replacing a category.")
record CategoryRequestDTO(

        @Schema(description = "Category name.", example = "Science Fiction")
        @NotBlank
        String name) {
}