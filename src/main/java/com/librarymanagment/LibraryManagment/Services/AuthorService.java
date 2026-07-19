package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.Author;
import com.librarymanagment.LibraryManagment.Repostries.AuthorRepository;
import com.librarymanagment.LibraryManagment.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.AuthorResponseDTO;
import com.librarymanagment.LibraryManagment.util.mapper.AuthorMapper;
import com.librarymanagment.LibraryManagment.util.GenericPatcher;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {


    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final GenericPatcher patchUtil;
    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper, GenericPatcher patchUtil) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.patchUtil = patchUtil;
    }


    @Transactional
    public AuthorResponseDTO saveAuthor(AuthorRequestDTO authorDTO) {
        Author newAuthor = authorMapper.mapRequestDTOToAuthor(authorDTO);
        Author savedAuthor = authorRepository.save(newAuthor);
        return authorMapper.mapAuthorToResponseDTO(savedAuthor);
    }


    @Transactional
    public AuthorResponseDTO updateAuthor(long id, AuthorRequestDTO requestDTO) {

        Author existingAuthor = findById(id);

        existingAuthor.setAuthorName(requestDTO.authorName());
        existingAuthor.setNationality(requestDTO.nationality());

        Author savedAuthor = authorRepository.save(existingAuthor);

        return authorMapper.mapAuthorToResponseDTO(savedAuthor);
    }


    public List<AuthorResponseDTO> findAll(){
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::mapAuthorToResponseDTO)
                .toList();
    }

    public Author findById(long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
    }

    public AuthorResponseDTO findByIdToResponse(long id){
        return authorMapper.mapAuthorToResponseDTO(authorRepository.findAuthorById(id).orElseThrow(() -> new EntityNotFoundException("Author with ID " + id + " has not been found ")));
    }


    @Transactional
    public void delete(Long id){
        int rowsAffected = authorRepository.deleteAuthorById(id);
        if(rowsAffected == 0) {
            throw new EntityNotFoundException("Author not found");
        }
    }


    @Transactional
    public AuthorResponseDTO patchAuthor(long id, String patchBody) {

        Author existingAuthor = findById(id);

        AuthorResponseDTO dtoToPatch = authorMapper.mapAuthorToResponseDTO(existingAuthor);

        AuthorResponseDTO patchedDto = patchUtil.applyPatch(patchBody, dtoToPatch, AuthorResponseDTO.class);

        existingAuthor.setAuthorName(patchedDto.authorName());
        existingAuthor.setNationality(patchedDto.nationality());

        return authorMapper.mapAuthorToResponseDTO(authorRepository.save(existingAuthor));
    }
}
