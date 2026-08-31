package com.librarymanagement.LibraryManagement.dto.Request;

import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @Size(min = 20, message = "The minimum size of the costumer name should be at least 20")
        String name){
}
