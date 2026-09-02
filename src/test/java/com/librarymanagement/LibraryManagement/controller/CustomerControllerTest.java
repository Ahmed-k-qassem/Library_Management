package com.librarymanagement.LibraryManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.Controller.CustomerController;

import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.dto.Response.CustomerResponseDTO;
import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.service.CustomerService;
import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.util.dto.request.CustomerRequestDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.dto.response.CustomerResponseDtoTestDataBuilder;
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

import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class})
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Access Test: no unauthenticated users can access")
    void givenNoToken_whenGetCustomers_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("Access Test: token with no recognised role is forbidden")
    void givenTokenWithoutRecognisedRole_whenGetCustomers_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/customers").with(outsider()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("Get mapping: Given User role, then ok")
    void givenPatronRole_whenGetCustomers_thenOk() throws Exception {
        CustomerResponseDTO customer = CustomerResponseDtoTestDataBuilder.getInstance().build();
        when(customerService.getAllCustomers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/customers").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(customer.name()));

        verify(customerService).getAllCustomers();
    }

    @Test
    @DisplayName("Get mapping: Given Admin role, then ok")
    void givenAdminRole_whenGetCustomers_thenOk() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/customers").with(admin()))
                .andExpect(status().isOk());

        verify(customerService).getAllCustomers();
    }

    @Test
    @DisplayName("Post mapping: no token, then unauthorized")
    void givenNoToken_whenAddCustomer_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                CustomerRequestDtoTestDataBuilder.getInstance().build())))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("Post mapping: Given User role when add customer, then created")
    void givenPatronRole_whenAddCustomer_thenCreated() throws Exception {
        var request = CustomerRequestDtoTestDataBuilder.getInstance().build();
        var response = CustomerResponseDtoTestDataBuilder.getInstance()
                .withName(request.name())
                .build();

        when(customerService.createCustomer(eq(request), eq(PATRON_UUID))).thenReturn(response);

        mockMvc.perform(post("/api/customers").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()));

        verify(customerService).createCustomer(request, PATRON_UUID);
    }

    @Test
    @DisplayName("Post mapping: caller's own subject claim is passed to the service")
    void givenAdminRole_whenAddCustomer_thenServiceReceivesTheCallersOwnSubjectClaim() throws Exception {
        var request = CustomerRequestDtoTestDataBuilder.getInstance().build();
        var response = CustomerResponseDtoTestDataBuilder.getInstance()
                .withUserUuid(ADMIN_UUID)
                .build();

        when(customerService.createCustomer(eq(request), eq(ADMIN_UUID))).thenReturn(response);

        mockMvc.perform(post("/api/customers").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userUuid").value(ADMIN_UUID));

        verify(customerService).createCustomer(request, ADMIN_UUID);
        verify(customerService, never()).createCustomer(any(), eq(PATRON_UUID));
    }

    @Test
    @DisplayName("Post mapping: short name currently still created (KNOWN_GAP)")
    void givenNameShorterThanMinimumSize_whenAddCustomer_thenCreated_KNOWN_GAP() throws Exception {
        var request = CustomerRequestDtoTestDataBuilder.getInstance().withName("A").build();
        var response = CustomerResponseDtoTestDataBuilder.getInstance().withName("A").build();

        when(customerService.createCustomer(eq(request), eq(PATRON_UUID))).thenReturn(response);

        mockMvc.perform(post("/api/customers").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(customerService).createCustomer(request, PATRON_UUID);
    }
}