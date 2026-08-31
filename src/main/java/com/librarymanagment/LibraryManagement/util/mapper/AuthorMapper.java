package com.librarymanagment.LibraryManagement.util.mapper;

import com.librarymanagment.LibraryManagement.entity.Author;
import com.librarymanagment.LibraryManagement.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.AuthorResponseDTO;
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
