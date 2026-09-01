package com.librarymanagement.LibraryManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.Controller.AuthorController;
import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.dto.Request.AuthorRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.AuthorResponseDTO;
import com.librarymanagement.LibraryManagement.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.service.AuthorService;
import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.util.dto.request.AuthorRequestDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.dto.response.AuthorResponseDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.entity.PatchBodyBuilder;
import com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.admin;
import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.patron;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import({SecurityConfig.class, KeycloakJwtTestSupport.class, GlobalExceptionHandler.class})
public class AuthorControllerTest {
    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Access test : Testing if the user can enter without a valid token")
    void givenNoToken_whenGetAuthors_thenUnAuthorized() throws Exception{
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authorService);
    }


    @Test
    @DisplayName("Get mapping test: Get authors method")
    void givenPatronUser_whenGetAuthors_thenOk() throws Exception{
        AuthorResponseDTO authorResponse = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withName("J3fr")
                .withId(1L)
                .build();

        when(authorService.findAll()).thenReturn(List.of(authorResponse));

        mockMvc.perform(get("/api/authors").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].authorName").value("J3fr"))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(authorService).findAll();
    }


    @Test
    @DisplayName("Get mapping test: get author by id method")
    void givenPatronUser_whenGetAuthorById_thenOk() throws Exception{
        Long authorId = 1L;
        AuthorResponseDTO author = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withName("Fibi chubi")
                .build();

        when(authorService.findByIdToResponse(authorId)).thenReturn(author);

        mockMvc.perform(get("/api/authors/1").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName").value("Fibi chubi"));

        verify(authorService).findByIdToResponse(authorId);
    }


    @Test
    @DisplayName("Post mapping: Add author method with USER role")
    void givenPatronUser_whenAddAuthor_thenForbidden() throws Exception {
        mockMvc.perform(post("/api/authors").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO().build())))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authorService);
    }

    @Test
    @DisplayName("Post mapping: Add author method with ADMIN role")
    void givenAdminUser_whenAddAuthor_thenOk() throws Exception {
        AuthorRequestDTO author = AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO().build();
        AuthorResponseDTO created = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withId(1L)
                .withName("default author")
                .build();

        when(authorService.saveAuthor(author)).thenReturn(created);

        mockMvc.perform(post("/api/authors").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.authorName").value("default author"));

        verify(authorService).saveAuthor(author);
    }


    @Test
    @DisplayName("Put mapping: Update author method with USER role")
    void givenPatronUser_whenPutAuthor_thenForbidden() throws Exception{
        AuthorRequestDTO author = AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO().build();
        mockMvc.perform(put("/api/authors/1").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authorService);
    }


    @Test
    @DisplayName("Put mapping: Update author method with ADMIN role")
    void givenAdminUser_whenPutAuthor_thenOk() throws Exception{
        Long authorId = 42L;
        AuthorRequestDTO request = AuthorRequestDtoTestDataBuilder.anAuthorRequestDTO()
                .withName("Ahmed")
                .withNationality("Indian")
                .build();
        AuthorResponseDTO expected = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto()
                .withId(authorId)
                .withName("Ahmed")
                .withNationality("Indian")
                .build();

        when(authorService.updateAuthor(authorId, request)).thenReturn(expected);

        mockMvc.perform(put("/api/authors/{id}", authorId).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expected.id()))
                .andExpect(jsonPath("$.authorName").value(expected.authorName()));

        verify(authorService).updateAuthor(authorId, request);
    }

    @Test
    @DisplayName("Delete mapping: Delete author method with USER role")
    void givenPatronUser_whenDeleteAuthor_thenForbidden() throws Exception{
        mockMvc.perform(delete("/api/authors/1").with(patron()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authorService);
    }


    @Test
    @DisplayName("Delete mapping: Delete author method with ADMIN role")
    void givenAdminUser_whenDeleteAuthor_thenNoResponseOk() throws Exception{
        Long authorId = 1L;

        mockMvc.perform(delete("/api/authors/1").with(admin()))
                .andExpect(status().isNoContent());

        verify(authorService).delete(authorId);
    }


    @Test
    @DisplayName("Patch mapping: patch author method with USER role")
    void givenPatronUser_whenPatchAuthor_thenForbidden() throws Exception{

        String patchBody = PatchBodyBuilder.getInstance().targetColumn("authorName").columnValue("fibiChubi").build();

        mockMvc.perform(patch("/api/authors/1").with(patron())
                .contentType("application/json-patch+json")
                .content(patchBody))
                .andExpect(status().isForbidden());

        verify(authorService, never()).patchAuthor(anyLong(), any());

    }

    @Test
    @DisplayName("Patch mapping: patch author method with ADMIN role")
    void givenAdminUser_whenPatchAuthor_thenOk() throws Exception{
        Long authorId = 1L;
        String patchBody = PatchBodyBuilder.getInstance().targetColumn("authorName").columnValue("fibiChubi").build();
        AuthorResponseDTO responseDTO = AuthorResponseDtoTestDataBuilder.anAuthorResponseDto().withName("fibiChubi").build();

        when(authorService.patchAuthor(authorId, patchBody)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/authors/1").with(admin())
                .contentType("application/json-patch+json")
                .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authorName").value(responseDTO.authorName()));

        verify(authorService).patchAuthor(authorId, patchBody);
    }

}
