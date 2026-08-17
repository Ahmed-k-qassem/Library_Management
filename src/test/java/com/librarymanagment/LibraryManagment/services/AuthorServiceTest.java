package com.librarymanagment.LibraryManagment.services;

import com.librarymanagment.LibraryManagment.Entities.Author;
import com.librarymanagment.LibraryManagment.Repostries.AuthorRepository;
import com.librarymanagment.LibraryManagment.Services.AuthorService;
import com.librarymanagment.LibraryManagment.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.AuthorResponseDTO;
import com.librarymanagment.LibraryManagment.util.GenericPatcher;
import com.librarymanagment.LibraryManagment.util.mapper.AuthorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private GenericPatcher patchUtil;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void saveAuthor_ShouldReturnResponseDTO() {

        AuthorRequestDTO request = new AuthorRequestDTO("Tolkien", "British");
        Author authorEntity = new Author(1L, "Tolkien", "British");
        AuthorResponseDTO expectedResponse = new AuthorResponseDTO(1L, "Tolkien", "British");

        when(authorMapper.mapRequestDTOToAuthor(request)).thenReturn(authorEntity);
        when(authorRepository.save(authorEntity)).thenReturn(authorEntity);
        when(authorMapper.mapAuthorToResponseDTO(authorEntity)).thenReturn(expectedResponse);

        AuthorResponseDTO actualResponse = authorService.saveAuthor(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);


        verify(authorRepository, times(1)).save(authorEntity);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {

        Long authorId = 1L;

        when(authorRepository.deleteAuthorById(authorId)).thenReturn(1);

        authorService.delete(authorId);

        verify(authorRepository, times(1)).deleteAuthorById(authorId);
    }


    @Test
    void findByIdToResponse_ShouldReturnAuthorResponseDTO() {
        Long authorId = 1L;
        Author mockAuthor = new Author(authorId, "George Orwell", "British");
        AuthorResponseDTO expectedDto = new AuthorResponseDTO(authorId, "George Orwell", "British");


        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));

        when(authorMapper.mapAuthorToResponseDTO(mockAuthor)).thenReturn(expectedDto);


        AuthorResponseDTO actualDto = authorService.findByIdToResponse(authorId);


        assertThat(actualDto).isNotNull();
        assertThat(actualDto.authorName()).isEqualTo(expectedDto.authorName());
        assertThat(actualDto.nationality()).isEqualTo(expectedDto.nationality());


        verify(authorRepository, times(1)).findById(authorId);
        verify(authorMapper, times(1)).mapAuthorToResponseDTO(mockAuthor);
    }


    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor(){
        Long authorId = 1L;
        Author mockAuthor = new Author(authorId, "Ahmed", "Syrian");
        AuthorResponseDTO expected = new AuthorResponseDTO(authorId, "j3fr", "Syrian");
        AuthorRequestDTO requestDTO = new AuthorRequestDTO("j3fr", "Syrian");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));
        when(authorMapper.mapAuthorToResponseDTO(mockAuthor)).thenReturn(expected);

        AuthorResponseDTO actualResponse = authorService.updateAuthor(authorId, requestDTO);

        assertThat(actualResponse).isEqualTo(expected);
        assertThat(mockAuthor.getAuthorName()).isEqualTo("j3fr");

        verify(authorRepository, times(1)).findById(authorId);
        verify(authorMapper, times(1)).mapAuthorToResponseDTO(mockAuthor);
    }
}