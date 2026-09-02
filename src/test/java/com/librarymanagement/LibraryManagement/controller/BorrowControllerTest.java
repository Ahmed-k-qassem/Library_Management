package com.librarymanagement.LibraryManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.Controller.BorrowController;
import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.dto.Request.BorrowRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import com.librarymanagement.LibraryManagement.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.service.BorrowService;
import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.util.dto.request.BorrowRequestDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.dto.response.BorrowResponseDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport;
import jakarta.persistence.EntityNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BorrowController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
public class BorrowControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BorrowService borrowService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Access Test: no unauthenticated users can access")
    void givenNoToken_whenAccessEndpoint_thenUnauthorized() throws Exception{
        mockMvc.perform(get("/api/borrow"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(borrowService);
    }

    @Test
    @DisplayName("Get mapping: Given User role, can't access")
    void givenPatronUser_whenGetBorrows_thenForbidden() throws Exception{
        mockMvc.perform(get("/api/borrow").with(patron()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(borrowService);
    }

    @Test
    @DisplayName("Get mapping: Given adming role, then ok")
    void givenAdminUser_whenGetBorrows_thenOk() throws Exception{
        BorrowResponseDTO borrow = BorrowResponseDtoTestDataBuilder.getInstance()
                .withBookTitle("Effective testing")
                .withCustomerName("Ahmed")
                .build();

        when(borrowService.getAllBorrows()).thenReturn(List.of(borrow));

        mockMvc.perform(get("/api/borrow").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerName").value("Ahmed"))
                .andExpect(jsonPath("$[0].bookTitle").value("Effective testing"));

        verify(borrowService).getAllBorrows();

    }


    @Test
    @DisplayName("Post mapping: Given user role when borrow book, then forbidden")
    void givenPatronUser_whenBorrowBook_thenForbidden() throws Exception{
        BorrowRequestDTO borrow = BorrowRequestDtoTestDataBuilder.getInstance().build();

        mockMvc.perform(post("/api/borrow").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrow)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(borrowService);
    }

    @Test
    @DisplayName("Post mapping: Given admin role when borrow book, then created")
    void givenAdminUser_whenBorrowBook_thenCreated() throws Exception{
        BorrowRequestDTO borrow = BorrowRequestDtoTestDataBuilder.getInstance().build();
        BorrowResponseDTO response = BorrowResponseDtoTestDataBuilder.getInstance()
                .withCustomerName("Ahmed")
                .withBookTitle("Effective testing")
                .build();

        when(borrowService.borrowBook(borrow)).thenReturn(response);

        mockMvc.perform(post("/api/borrow").with(admin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrow)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Effective testing"))
                .andExpect(jsonPath("$.customerName").value("Ahmed"));

        verify(borrowService).borrowBook(borrow);
    }


    @Test
    @DisplayName("Post mapping: borrowing a non-existent book returns 404")
    void givenAdminRole_whenBookDoesNotExist_thenNotFoundWithHttpDtoEnvelope() throws Exception {
        BorrowRequestDTO borrow = BorrowRequestDtoTestDataBuilder.getInstance().build();
        when(borrowService.borrowBook(borrow))
                .thenThrow(new EntityNotFoundException("Book not found"));

        mockMvc.perform(post("/api/borrow").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrow)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }
}
