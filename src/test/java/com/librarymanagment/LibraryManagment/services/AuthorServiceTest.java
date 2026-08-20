package com.librarymanagment.LibraryManagment.services;

import com.librarymanagment.LibraryManagment.Entities.Author;
import com.librarymanagment.LibraryManagment.Repostries.AuthorRepository;
import com.librarymanagment.LibraryManagment.Services.AuthorService;
import com.librarymanagment.LibraryManagment.dto.Request.AuthorRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.AuthorResponseDTO;
import com.librarymanagment.LibraryManagment.exception.JsonPatchProcessingException;
import com.librarymanagment.LibraryManagment.util.dto.request.AuthorRequestDtoTestDataBuilder;
import com.librarymanagment.LibraryManagment.util.dto.response.AuthorResponseDtoTestDataBuilder;
import com.librarymanagment.LibraryManagment.util.entity.AuthorTestDataBuilder;
import com.librarymanagment.LibraryManagment.util.GenericPatcher;
import com.librarymanagment.LibraryManagment.util.entity.GenericPatcherTestDataBuilder;
import com.librarymanagment.LibraryManagment.util.entity.PatchBodyBuilder;
import com.librarymanagment.LibraryManagment.util.mapper.AuthorMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

        AuthorRequestDTO request = AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO().build();
        Author authorEntity = AuthorTestDataBuilder.anAuthor().build();
        AuthorResponseDTO expectedResponse = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto().build();

        when(authorMapper.mapRequestDTOToAuthor(request)).thenReturn(authorEntity);
        when(authorRepository.save(authorEntity)).thenReturn(authorEntity);
        when(authorMapper.mapAuthorToResponseDTO(authorEntity)).thenReturn(expectedResponse);

        AuthorResponseDTO actualResponse = authorService.saveAuthor(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);


        verify(authorRepository, times(1)).save(authorEntity);
    }


    @Test
    void saveAuthor_ShouldThrowException_WhenRequestIsNull(){
        AuthorRequestDTO request = null;

        assertThatThrownBy(() -> authorService.saveAuthor(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dev error: Author request id cannot be null");

        verify(authorRepository, times(0)).save(any());
        verifyNoInteractions(authorRepository);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {

        Long authorId = 1L;

        when(authorRepository.deleteAuthorById(authorId)).thenReturn(1);

        authorService.delete(authorId);

        verify(authorRepository, times(1)).deleteAuthorById(authorId);
    }

    @Test
    void delete_ShouldTrowException_WhenAuthorIdIsWrong(){
        Long authorId = 999L;

        assertThatThrownBy(() -> authorService.delete(authorId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author not found");

        verify(authorRepository, times(1)).deleteAuthorById(authorId);
    }



    @Test
    void findByIdToResponse_ShouldReturnAuthorResponseDTO() {
        Long authorId = 1L;
        Author mockAuthor = AuthorTestDataBuilder.anAuthor().build();
        AuthorResponseDTO expectedDto = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto().build();


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
    void findByIdResponse_ShouldThrowException_WhenNoAuthorFound(){
        Long authorId = 1L;

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.findByIdToResponse(authorId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author not found");

        verify(authorRepository, times(1)).findById(authorId);
        verify(authorMapper, times(0)).mapAuthorToResponseDTO(any());
    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor(){
        Long authorId = 1L;
        Author mockAuthor = AuthorTestDataBuilder.anAuthor().build();
        AuthorResponseDTO expected = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withName("j3fr").build();
        AuthorRequestDTO requestDTO = AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO()
                .withName("j3fr").build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));
        when(authorMapper.mapAuthorToResponseDTO(mockAuthor)).thenReturn(expected);
        when(authorRepository.save(mockAuthor)).thenReturn(mockAuthor);

        AuthorResponseDTO actualResponse = authorService.updateAuthor(authorId, requestDTO);

        assertThat(actualResponse).isEqualTo(expected);
        assertThat(mockAuthor.getAuthorName()).isEqualTo("j3fr");

        verify(authorRepository, times(1)).findById(authorId);
        verify(authorMapper, times(1)).mapAuthorToResponseDTO(mockAuthor);
        verify(authorRepository, times(1)).save(mockAuthor);

    }


    @Test
    void updateAuthor_ShouldThrowException_WhenAuthorNotFound() {

        Long invalidAuthorId = 999L;


        AuthorRequestDTO requestDTO = AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO()
                .withName("j3fr")
                .withNationality("Syrian")
                .build();


        when(authorRepository.findById(invalidAuthorId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> authorService.updateAuthor(invalidAuthorId, requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author not found");

        verify(authorRepository, times(1)).findById(invalidAuthorId);

        verify(authorMapper, never()).mapAuthorToResponseDTO(any());
    }



    @Test
    void patchAuthor_ShouldMutateStateAndReturnResponse() {
        Long authorId = 1L;
        String patchBody = PatchBodyBuilder.getInstance()
                .withAttribute("authorName")
                .withValue("fibi nono")
                .build();

        Author existingAuthor = AuthorTestDataBuilder.anAuthor()
                .withAuthorName("nono")
                .withNationality("syrian")
                .build();

        AuthorResponseDTO dtoToPatch = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withName("nono")
                .withNationality("syrian")
                .build();

        AuthorResponseDTO patchedDto = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withName("fibi nono")
                .withNationality("syrian")
                .build();

        AuthorResponseDTO expectedResponse = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withName("fibi nono")
                .withNationality("syrian")
                .build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));

        when(authorMapper.mapAuthorToResponseDTO(existingAuthor))
                .thenReturn(dtoToPatch, expectedResponse);

        when(patchUtil.applyPatch(patchBody, dtoToPatch, AuthorResponseDTO.class))
                .thenReturn(patchedDto);

        AuthorResponseDTO actual = authorService.patchAuthor(authorId, patchBody);

        assertThat(actual).isEqualTo(expectedResponse);
        assertThat(existingAuthor.getAuthorName()).isEqualTo("fibi nono");
        assertThat(existingAuthor.getNationality()).isEqualTo("syrian");

        verify(authorRepository).findById(authorId);
        verify(patchUtil).applyPatch(patchBody, dtoToPatch, AuthorResponseDTO.class);
        verify(authorMapper, times(2)).mapAuthorToResponseDTO(existingAuthor);

        verifyNoMoreInteractions(authorRepository, authorMapper, patchUtil);
    }


    @Test
    void patchAuthor_ShouldThrowException_ForNullPatchBody(){
        Long authorId = 1L;
        String patchBody = null;

        assertThatThrownBy(() -> authorService.patchAuthor(authorId, patchBody))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dev error: The patch body sent is null");


        verifyNoInteractions(authorRepository, authorMapper, patchUtil);
    }

    @Test
    void patchAuthor_ShouldThrowException_WhenPatchBodyIsInvalid() {
        Long authorId = 1L;
        String bustedPatchBody = "straight up garbage json";

        Author existingAuthor = AuthorTestDataBuilder.anAuthor().build();
        AuthorResponseDTO dtoToPatch = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto().build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorMapper.mapAuthorToResponseDTO(existingAuthor)).thenReturn(dtoToPatch);

        when(patchUtil.applyPatch(bustedPatchBody, dtoToPatch, AuthorResponseDTO.class))
                .thenThrow(new JsonPatchProcessingException("Failed to apply patch: Unrecognized token"));

        assertThatThrownBy(() -> authorService.patchAuthor(authorId, bustedPatchBody))
                .isInstanceOf(JsonPatchProcessingException.class)
                .hasMessageContaining("Failed to apply patch");

        verify(authorRepository).findById(authorId);
        verify(authorMapper, times(1)).mapAuthorToResponseDTO(existingAuthor);
        verify(patchUtil).applyPatch(bustedPatchBody, dtoToPatch, AuthorResponseDTO.class);

        verifyNoMoreInteractions(authorRepository, authorMapper, patchUtil);
    }

}