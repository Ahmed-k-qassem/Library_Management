package com.librarymanagment.LibraryManagement.service;

import com.librarymanagment.LibraryManagement.entity.Author;
import com.librarymanagment.LibraryManagement.repository.AuthorRepository;
import com.librarymanagment.LibraryManagement.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.AuthorResponseDTO;
import com.librarymanagment.LibraryManagement.util.mapper.AuthorMapper;
import com.librarymanagment.LibraryManagement.util.GenericPatcher;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
        Assert.notNull(authorDTO, "Dev error: Author request id cannot be null");
        Author newAuthor = authorMapper.mapRequestDTOToAuthor(authorDTO);
        Author savedAuthor = authorRepository.save(newAuthor);
        return authorMapper.mapAuthorToResponseDTO(savedAuthor);
    }

    @Transactional
    public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO requestDTO) {
        Assert.notNull(requestDTO, "Dev error: The request dto cannot be null");
        Author existingAuthor = findById(id);
        existingAuthor.setAuthorName(requestDTO.authorName());
        existingAuthor.setNationality(requestDTO.nationality());
        return authorMapper.mapAuthorToResponseDTO(authorRepository.save(existingAuthor));
    }

    public List<AuthorResponseDTO> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::mapAuthorToResponseDTO)
                .toList();
    }

    public Author findById(Long id) {
        Assert.notNull(id, "Dev error: The id cannot be null");
        Assert.isTrue(id > 0, "Dev error: The id should be positive or above 0");
        return authorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Author not found"));
    }

    public AuthorResponseDTO findByIdToResponse(Long id) {
        Assert.notNull(id, "Dev error: The id cannot be null");
        Assert.isTrue(id > 0, "Dev error: The id should be positive or above 0");
        return authorMapper.mapAuthorToResponseDTO(findById(id));
    }

    @Transactional
    public void delete(Long id) {
        Assert.notNull(id, "Dev error: The id cannot be null");
        Assert.isTrue(id > 0, "Dev error: Id should be positive or above 0");
        int rowsAffected = authorRepository.deleteAuthorById(id);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Author not found");
        }
    }

    @Transactional
    public AuthorResponseDTO patchAuthor(Long id, String patchBody) {
        Assert.notNull(patchBody, "Dev error: The patch body sent is null");
        Author existingAuthor = findById(id);
        AuthorResponseDTO dtoToPatch = authorMapper.mapAuthorToResponseDTO(existingAuthor);
        AuthorResponseDTO patchedDto = patchUtil.applyPatch(patchBody, dtoToPatch, AuthorResponseDTO.class);
        existingAuthor.setAuthorName(patchedDto.authorName());
        existingAuthor.setNationality(patchedDto.nationality());
        return authorMapper.mapAuthorToResponseDTO(existingAuthor);
    }
}