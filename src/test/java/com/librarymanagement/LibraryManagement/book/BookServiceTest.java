package com.librarymanagement.LibraryManagement.book;

import com.librarymanagement.LibraryManagement.author.AuthorService;
import com.librarymanagement.LibraryManagement.category.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("Positive Testing for find books for specific author")
    void getBooksForAuthor_ShouldReturnBooksForValidAuthor(){

        long authorId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<BookAuthorResponseDTO> mockedPage = new PageImpl<>(List.of(
                BookAuthorResponseTestDataBuilder.getInstance().withTitle("fibi_chubi").build(),
                BookAuthorResponseTestDataBuilder.getInstance().withTitle("Lobi_fiba").build()),
                pageable, 2);
        when(bookRepository.getBooksByAuthorId(authorId, pageable)).thenReturn(mockedPage);


        Page<BookAuthorResponseDTO> real = bookService.getBooksForAuthor(authorId, pageable);


        assertThat(real).isEqualTo(mockedPage);

        verify(bookRepository, times(1)).getBooksByAuthorId(authorId, pageable);
    }

    @Test
    @DisplayName("Negative testing for find books for specific author")
    void getBooksForAuthor_ShouldReturnEmptyPage_WhenAuthorIdHasNoBooks(){
        long authorId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<BookAuthorResponseDTO> mockedPage = Page.empty(pageable);

        when(bookRepository.getBooksByAuthorId(authorId, pageable)).thenReturn(mockedPage);

        Page<BookAuthorResponseDTO> real = bookService.getBooksForAuthor(authorId, pageable);

        assertThat(real.getContent()).isEmpty();
        assertThat(real.getTotalElements()).isZero();

        verify(bookRepository, times(1)).getBooksByAuthorId(authorId, pageable);
    }

    @Test
    @DisplayName("findAll forwards the pageable unchanged and keeps the page metadata")
    void givenPageable_whenFindAll_thenForwardsItUnchangedAndKeepsMetadata() {
        Pageable pageable = PageRequest.of(2, 10, Sort.by("title").and(Sort.by("id")));
        Page<BookResponseDTO> stubbed = new PageImpl<>(
                List.of(BookResponseDtoTestDataBuilder.getInstance().build()), pageable, 21);
        when(bookRepository.findAllSummaries(pageable)).thenReturn(stubbed);

        Page<BookResponseDTO> result = bookService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(21);
        assertThat(result.getNumber()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(10);
        verify(bookRepository).findAllSummaries(pageable);
        verifyNoInteractions(bookMapper);
    }
}