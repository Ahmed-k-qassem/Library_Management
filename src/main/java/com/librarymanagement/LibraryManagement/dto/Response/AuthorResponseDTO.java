package com.librarymanagement.LibraryManagement.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "An author as returned by the API.")
public record AuthorResponseDTO(

        @Schema(
                description = "Generated database id.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        long id,

        @Schema(description = "The author's full name.", example = "Ghassan Kanafani")
        String authorName,

        @Schema(description = "The author's nationality.", example = "Palestinian")
        String nationality) {
}