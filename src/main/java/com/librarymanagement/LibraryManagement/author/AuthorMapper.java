package com.librarymanagement.LibraryManagement.author;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
class AuthorMapper {
    public AuthorResponseDTO mapAuthorToResponseDTO(Author author) {
        return new AuthorResponseDTO(author.getId(),author.getAuthorName(), author.getNationality());
    }


    public Author mapRequestDTOToAuthor(AuthorRequestDTO authorRequestDTO) {
        return new Author(authorRequestDTO.authorName(),authorRequestDTO.nationality());
    }
}
