package com.librarymanagement.LibraryManagement.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ApiError",
        description = "Standard error body returned by the global exception handler.")
public class HttpDTO {

    @Schema(
            description = "Human-readable explanation. Comes from the exception message, so the "
                    + "exact wording is not part of the API contract — do not parse it.",
            example = "Author not found with id: 42")
    private String message;

    @Schema(
            description = "The HTTP status code, repeated in the body for convenience.",
            example = "404")
    private int statusCode;

    public HttpDTO(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}