package com.librarymanagment.LibraryManagment.dto.Response;

import com.librarymanagment.LibraryManagment.entity.Status;

import java.util.Date;

public record BookResponseDTO(long id, String title, String isbn, int pageCount, Status status, Date addedDate, String authorName, String categoryName) {
}
