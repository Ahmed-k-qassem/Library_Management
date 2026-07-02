package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.Author;
import com.librarymanagment.LibraryManagment.Repostries.AuthorRepository;
import com.librarymanagment.LibraryManagment.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.AuthorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {


    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors(){
        return authorRepository.findAll();
    }

    @Transactional
    public Author saveAuthor(AuthorRequestDTO author){
        Author createdAuthor = new Author();

        createdAuthor.setAuthorName(author.authorName());
        createdAuthor.setNationality(author.nationality());
        return authorRepository.save(createdAuthor);
    }

    @Transactional
    public Author saveAuthor(Author author){
        return authorRepository.save(author);
    }


    @Transactional
    public Author saveAuthor(AuthorResponseDTO author){
        Author createdAuthor = new Author();

        createdAuthor.setId(author.id());
        createdAuthor.setAuthorName(author.authorName());
        createdAuthor.setNationality(author.nationality());
        return authorRepository.save(createdAuthor);
    }


    public List<Author> findAll(){
        return authorRepository.findAll();
    }


    public Author findById(long id){
        return authorRepository.findAuthorById(id).orElseThrow(() -> new EntityNotFoundException("Author with ID " + id + " has not been found "));
    }


    @Transactional
    public void delete(Long id){
        int rowsAffected = authorRepository.deleteAuthorById(id);
        if(rowsAffected == 0) {
            throw new EntityNotFoundException("Author not found");
        }
    }


    public AuthorRequestDTO castToAuthorRequestDTO(Author author){
        return new AuthorRequestDTO(author.getAuthorName(), author.getNationality());
    }

    public AuthorResponseDTO castToAuthorResponseDTO(Author author){
        return new AuthorResponseDTO(author.getId(), author.getAuthorName(), author.getNationality());
    }
}
