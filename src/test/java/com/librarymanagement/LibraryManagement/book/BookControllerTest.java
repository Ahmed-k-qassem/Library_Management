package com.librarymanagement.LibraryManagement.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.common.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.common.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.common.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.user.UserService;
import jakarta.persistence.EntityNotFoundException;
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

import static com.librarymanagement.LibraryManagement.common.security.KeycloakJwtTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration(DataWebAutoConfiguration.class)
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
        when(bookService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(BookResponseDtoTestDataBuilder.getInstance().build()),
                        PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/books").with(patron()))
                .andExpect(status().isOk());

        verify(bookService).findAll(any(Pageable.class));
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
        when(bookService.findAll(any(Pageable.class))).thenReturn(Page.empty(PageRequest.of(0, 20)));

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


    @Test
    void givenPatronRole_whenGetBooksById_thenOk() throws Exception{
        BookResponseDTO book = BookResponseDtoTestDataBuilder.getInstance().withTitle("fibi nono").build();
        when(bookService.getBookResponseById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("fibi nono"));

        verify(bookService).getBookResponseById(1L);
    }

    @Test
    void givenAdminRole_whenDeleteBook_thenNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/1").with(admin()))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBookById(1L);
    }

    @Test
    void givenPatronRole_whenGetBooksForAuthor_thenOk() throws Exception{
        Long authorId = 1L;
        BookAuthorResponseDTO book = BookAuthorResponseTestDataBuilder.getInstance().
                withTitle("Fibi nono")
                .withIsbn("1292-2049")
                .build();
        when(bookService.getBooksForAuthor(eq(authorId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(book), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/books/author/1").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookName").value("Fibi nono"))
                .andExpect(jsonPath("$.content[0].isbn").value("1292-2049"));
        verify(bookService).getBooksForAuthor(eq(authorId), any(Pageable.class));
    }


    @Test
    void givenNoPagingParams_whenGetBooks_thenDefaultsWithTitleSortAndIdTiebreaker() throws Exception {
        when(bookService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/books").with(patron()))
                .andExpect(status().isOk());

        Pageable sent = capturePageableSentToFindAll();
        assertThat(sent.getPageNumber()).isZero();
        assertThat(sent.getPageSize()).isEqualTo(20);
        assertThat(sent.getSort()).containsExactly(Sort.Order.asc("title"), Sort.Order.asc("id"));
    }

    @Test
    void givenPageSizeAndSortParams_whenGetBooks_thenPassedThroughWithIdTiebreaker() throws Exception {
        when(bookService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/books")
                        .param("page", "2")
                        .param("size", "10")
                        .param("sort", "addedDate,desc")
                        .with(patron()))
                .andExpect(status().isOk());

        Pageable sent = capturePageableSentToFindAll();
        assertThat(sent.getPageNumber()).isEqualTo(2);
        assertThat(sent.getPageSize()).isEqualTo(10);
        assertThat(sent.getSort()).containsExactly(Sort.Order.desc("addedDate"), Sort.Order.asc("id"));
    }

    @Test
    void givenOversizedPage_whenGetBooks_thenClampedToMaxPageSize() throws Exception {
        when(bookService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/books").param("size", "5000").with(patron()))
                .andExpect(status().isOk());

        assertThat(capturePageableSentToFindAll().getPageSize()).isEqualTo(100);
    }

    @Test
    void givenDisallowedSortField_whenGetBooks_thenBadRequestAndServiceNeverCalled() throws Exception {
        mockMvc.perform(get("/api/books").param("sort", "author.nationality").with(patron()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(startsWith("Invalid sort property")));

        verifyNoInteractions(bookService);
    }

    @Test
    void givenOneGoodAndOneBadSortField_whenGetBooks_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("sort", "title,asc")
                        .param("sort", "password,desc")
                        .with(patron()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }

    @Test
    void givenMiddlePageFromService_whenGetBooks_thenEnvelopeCarriesAllMetadata() throws Exception {
        when(bookService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(
                List.of(BookResponseDtoTestDataBuilder.getInstance().withTitle("Dune").build()),
                PageRequest.of(1, 1), 3));

        mockMvc.perform(get("/api/books").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Dune"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void givenEmptyPageFromService_whenGetBooks_thenOkWithEmptyContent() throws Exception {
        when(bookService.findAll(any(Pageable.class))).thenReturn(Page.empty(PageRequest.of(9, 20)));

        mockMvc.perform(get("/api/books").param("page", "9").with(patron()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void givenSortFieldNotAllowedForAuthorBooks_whenGetBooksForAuthor_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/books/author/1").param("sort", "addedDate").with(patron()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }

    private Pageable capturePageableSentToFindAll() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(bookService).findAll(captor.capture());
        return captor.getValue();
    }
}