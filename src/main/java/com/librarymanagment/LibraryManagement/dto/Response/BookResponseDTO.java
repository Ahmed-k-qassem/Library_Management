package com.librarymanagment.LibraryManagement.dto.Response;

import com.librarymanagment.LibraryManagement.entity.Status;

import java.util.Date;

public record BookResponseDTO(long id, String title, String isbn, int pageCount, Status status, Date addedDate, String authorName, String categoryName) {
}
