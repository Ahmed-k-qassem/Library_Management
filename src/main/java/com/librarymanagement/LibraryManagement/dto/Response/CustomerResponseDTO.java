package com.librarymanagement.LibraryManagement.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A customer as returned by the API.")
public record CustomerResponseDTO(

        @Schema(description = "Generated database id.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        long customerId,

        @Schema(description = "Full name.", example = "Mahmoud Abdel Rahman Youssef")
        String name,

        @Schema(description = "Keycloak subject (sub claim) this customer is linked to.", example = "9f6c1e2a-4b7d-4f1e-9c33-0a2b8d5e7f11", accessMode = Schema.AccessMode.READ_ONLY)
        String userUuid) {
}