package com.librarymanagement.LibraryManagement.dto.Response;

import com.librarymanagement.LibraryManagement.entity.Status;

public record BookAuthorResponseDTO(String bookName, String isbn, int pageCount, Status status, String authorName){}
