package com.librarymanagment.LibraryManagment.util.mapper;

import com.librarymanagment.LibraryManagment.entity.Author;
import com.librarymanagment.LibraryManagment.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.AuthorResponseDTO;
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
