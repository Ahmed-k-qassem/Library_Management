package com.librarymanagement.LibraryManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.Controller.BookController;
import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.service.BookService;
import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.util.dto.request.BookRequestDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.dto.response.BookResponseDtoTestDataBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.*;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    BookService bookService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void givenNoToken_whenGetBooks_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", startsWith("Bearer")));

        verifyNoInteractions(bookService);
    }

    @Test
    void givenTokenWithoutRecognisedRole_whenGetBooks_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/books").with(outsider()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

    @Test
    void givenPatronRole_whenGetBooks_thenOk() throws Exception {
        when(bookService.findAllBooks())
                .thenReturn(List.of(BookResponseDtoTestDataBuilder.getInstance().build()));

        mockMvc.perform(get("/api/books").with(patron()))
                .andExpect(status().isOk());

        verify(bookService).findAllBooks();
    }

    @Test
    void givenPatronRole_whenAddBook_thenForbidden() throws Exception {
        mockMvc.perform(post("/api/books").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                BookRequestDtoTestDataBuilder.getInstance().build())))
                .andExpect(status().isForbidden());

        verify(bookService, never()).createBook(any());
    }

    @Test
    void givenAdminRole_whenAddBook_thenCreated() throws Exception {
        when(bookService.createBook(any()))
                .thenReturn(BookResponseDtoTestDataBuilder.getInstance().build());

        mockMvc.perform(post("/api/books").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                BookRequestDtoTestDataBuilder.getInstance().build())))
                .andExpect(status().isCreated());

        verify(bookService).createBook(any());
    }

    @Test
    void givenNoToken_whenDeleteBook_thenUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isUnauthorized());

        verify(bookService, never()).deleteBookById(anyLong());
    }

    @Test
    void givenPatronRole_whenDeleteBook_thenUnAuthorized() throws Exception {
        mockMvc.perform(delete("/api/books/1").with(patron()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

    @Test
    void givenTokenWithoutRealmAccessClaim_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/books").with(withoutRealmAccessClaim(PATRON_UUID)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

    @Test
    void givenRealmAccessPresentButRolesListEmpty_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/books").with(withEmptyRoles(PATRON_UUID)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

    @Test
    void givenAuthenticatedPatron_whenGetBooks_thenUserIsSynchronised() throws Exception {
        when(bookService.findAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/api/books").with(patron()))
                .andExpect(status().isOk());

        verify(userService).syncUser(PATRON_UUID, PATRON_USERNAME, "USER");
    }

    @Test
    void givenNoToken_whenGetBooks_thenNoSynchronisationAttempted() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).syncUser(any(), any(), any());
    }

    @Test
    void givenAdminRole_whenAddBookWithShortTitle_thenBadRequest() throws Exception {
        var invalid = BookRequestDtoTestDataBuilder.getInstance().withTitle("Short").build();

        mockMvc.perform(post("/api/books").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());

        verify(bookService, never()).createBook(any());
    }

    @Test
    void givenPatronRole_whenBookDoesNotExist_thenNotFoundWithHttpDtoEnvelope() throws Exception {
        when(bookService.getBookResponseById(99L))
                .thenThrow(new EntityNotFoundException("Book not found with id 99"));

        mockMvc.perform(get("/api/books/99").with(patron()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Book not found with id 99"));
    }
}