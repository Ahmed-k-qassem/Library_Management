package com.librarymanagment.LibraryManagment.dto.Response;

import com.librarymanagment.LibraryManagment.Entities.Status;

public record BookAuthorResponseDTO(String bookName, String isbn, int pageCount, Status status, String authorName){}
