package com.librarymanagement.LibraryManagement.borrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.common.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.common.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.common.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.librarymanagement.LibraryManagement.common.security.KeycloakJwtTestSupport.admin;
import static com.librarymanagement.LibraryManagement.common.security.KeycloakJwtTestSupport.patron;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BorrowController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration(DataWebAutoConfiguration.class)
class BorrowControllerTest {
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

        when(borrowService.getAllBorrows(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(borrow), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/borrow").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].customerName").value("Ahmed"))
                .andExpect(jsonPath("$.content[0].bookTitle").value("Effective testing"));

        verify(borrowService).getAllBorrows(any(Pageable.class));

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

    @Test
    @DisplayName("Paging: no params -> page 0, size 20, newest first, id DESC tiebreaker")
    void givenNoPagingParams_whenGetBorrows_thenNewestFirstWithIdTiebreaker() throws Exception {
        when(borrowService.getAllBorrows(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/borrow").with(admin()))
                .andExpect(status().isOk());

        Pageable sent = capturePageableSentToService();
        assertThat(sent.getPageNumber()).isZero();
        assertThat(sent.getPageSize()).isEqualTo(20);
        assertThat(sent.getSort()).containsExactly(
                Sort.Order.desc("borrowDate"), Sort.Order.desc("id"));
    }

    @Test
    @DisplayName("Paging: page, size and an explicit ascending sort are passed through")
    void givenPageSizeAndAscendingSort_whenGetBorrows_thenPassedThroughWithIdTiebreaker() throws Exception {
        when(borrowService.getAllBorrows(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/borrow")
                        .param("page", "3")
                        .param("size", "5")
                        .param("sort", "borrowDate,asc")
                        .with(admin()))
                .andExpect(status().isOk());

        Pageable sent = capturePageableSentToService();
        assertThat(sent.getPageNumber()).isEqualTo(3);
        assertThat(sent.getPageSize()).isEqualTo(5);
        assertThat(sent.getSort()).containsExactly(
                Sort.Order.asc("borrowDate"), Sort.Order.desc("id"));
    }

    @Test
    @DisplayName("Paging: size=5000 is clamped to max-page-size (100)")
    void givenOversizedPage_whenGetBorrows_thenClampedToMaxPageSize() throws Exception {
        when(borrowService.getAllBorrows(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/borrow").param("size", "5000").with(admin()))
                .andExpect(status().isOk());

        assertThat(capturePageableSentToService().getPageSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("Sort: a DTO field name (bookTitle) is not an entity path and is rejected")
    void givenDtoFieldAsSort_whenGetBorrows_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/borrow").param("sort", "bookTitle").with(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(startsWith("Invalid sort property")));

        verifyNoInteractions(borrowService);
    }

    @Test
    @DisplayName("Sort: an association path into Customer -> User is rejected before any query")
    void givenAssociationPathSort_whenGetBorrows_thenBadRequestAndServiceNeverCalled() throws Exception {
        mockMvc.perform(get("/api/borrow").param("sort", "customer.user.keycloakUserId").with(admin()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(borrowService);
    }

    @Test
    @DisplayName("Security runs before sort validation: a patron with a bad sort still gets 403, not 400")
    void givenPatronWithBadSort_whenGetBorrows_thenForbiddenNotBadRequest() throws Exception {
        mockMvc.perform(get("/api/borrow").param("sort", "password").with(patron()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(borrowService);
    }

    @Test
    @DisplayName("Envelope: every pagination field is present in the JSON")
    void givenMiddlePageFromService_whenGetBorrows_thenEnvelopeCarriesAllMetadata() throws Exception {
        BorrowResponseDTO borrow = BorrowResponseDtoTestDataBuilder.getInstance()
                .withBookTitle("Dune").withCustomerName("Layla").build();
        when(borrowService.getAllBorrows(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(borrow), PageRequest.of(1, 1), 3));

        mockMvc.perform(get("/api/borrow").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].bookTitle").value("Dune"))
                .andExpect(jsonPath("$.content[0].customerName").value("Layla"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    @DisplayName("Envelope: a page past the end is 200 with empty content, not 404")
    void givenPagePastTheEnd_whenGetBorrows_thenOkWithEmptyContent() throws Exception {
        when(borrowService.getAllBorrows(any(Pageable.class))).thenReturn(Page.empty(PageRequest.of(9, 20)));

        mockMvc.perform(get("/api/borrow").param("page", "9").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    private Pageable capturePageableSentToService() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(borrowService).getAllBorrows(captor.capture());
        return captor.getValue();
    }
}