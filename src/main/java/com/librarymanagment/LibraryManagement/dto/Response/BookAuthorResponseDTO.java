package com.librarymanagment.LibraryManagement.dto.Response;

import com.librarymanagment.LibraryManagement.entity.Status;

public record BookAuthorResponseDTO(String bookName, String isbn, int pageCount, Status status, String authorName){}
