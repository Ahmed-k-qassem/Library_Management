package com.librarymanagement.LibraryManagement.util.mapper;

import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.dto.Request.AuthorRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.AuthorResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
    public AuthorResponseDTO mapAuthorToResponseDTO(Author author) {
        return new AuthorResponseDTO(author.getId(),author.getAuthorName(), author.getNationality());
    }


    public Author mapRequestDTOToAuthor(AuthorRequestDTO authorRequestDTO) {
        return new Author(authorRequestDTO.authorName(),authorRequestDTO.nationality());
    }
}
