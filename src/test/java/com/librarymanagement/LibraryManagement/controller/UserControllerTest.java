package com.librarymanagement.LibraryManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.Controller.UserController;
import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.dto.Response.UserResponseDTO;
import com.librarymanagement.LibraryManagement.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.util.dto.response.UserResponseDtoTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("Access test: only authenticated users can access the endpoints")
    void givenNoToken_whenAccessEndpoint_thenUnauthorized() throws Exception{
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get mapping: find all users with USER role")
    void givenPatronUser_whenFindAll_thenOk() throws Exception{
        UserResponseDTO user = UserResponseDtoTestDataBuilder.getInstance()
                .withUsername("Ahmedino")
                .withKeycloakUUID("1234-8271")
                .build();
        when(userService.findAll()).thenReturn(List.of(user));
        mockMvc.perform(get("/api/users").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("Ahmedino"))
                .andExpect(jsonPath("$[0].uuid").value("1234-8271"));

        verify(userService).findAll();
    }


    @Test
    void givenNoToken_whenDeleteUser_thenUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/admin/some-uuid"))
                .andExpect(status().isUnauthorized());
    }



}
