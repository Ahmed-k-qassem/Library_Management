package com.librarymanagment.LibraryManagment.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotNull
        @Size(min = 5, max = 20)
        String username) {
}