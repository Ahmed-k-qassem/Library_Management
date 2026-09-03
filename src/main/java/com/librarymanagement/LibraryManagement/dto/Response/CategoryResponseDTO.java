package com.librarymanagement.LibraryManagement.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A category as returned by the API.")
public record CategoryResponseDTO(

        @Schema(description = "Generated database id.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        long id,

        @Schema(description = "Category name.", example = "Science Fiction")
        String name) {
}