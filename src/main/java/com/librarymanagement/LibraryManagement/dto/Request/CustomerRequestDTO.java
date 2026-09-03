package com.librarymanagement.LibraryManagement.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for registering the caller as a customer.")
public record CustomerRequestDTO(

        @Schema(description = "Full name. Must be at least 20 characters.", example = "Mahmoud Abdel Rahman Youssef")
        @Size(min = 20, message = "The minimum size of the costumer name should be at least 20")
        String name) {
}