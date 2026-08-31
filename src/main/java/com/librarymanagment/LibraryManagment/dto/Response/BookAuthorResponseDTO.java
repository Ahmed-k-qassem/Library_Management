package com.librarymanagment.LibraryManagment.dto.Response;

import com.librarymanagment.LibraryManagment.entity.Status;

public record BookAuthorResponseDTO(String bookName, String isbn, int pageCount, Status status, String authorName){}
