package com.librarymanagement.LibraryManagement.service;

import com.librarymanagement.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import com.librarymanagement.LibraryManagement.repository.BookRepository;
import com.librarymanagement.LibraryManagement.util.dto.response.BookAuthorResponseTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.mapper.BookMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
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

        Long authorId = 1L;
        List<BookAuthorResponseDTO> mockedList = List.of(
                BookAuthorResponseTestDataBuilder.getInstance().withTitle("fibi_chubi").build(),
                BookAuthorResponseTestDataBuilder.getInstance().withTitle("Lobi_fiba").build());
        when(bookRepository.getBooksByAuthorId(authorId)).thenReturn(mockedList);


        List<BookAuthorResponseDTO> real = bookService.getBooksForAuthor(authorId);


        assertThat(real).isEqualTo(mockedList);

        verify(bookRepository, times(1)).getBooksByAuthorId(authorId);
    }

    @Test
    @DisplayName("Negative testing for find books for specific author")
    void getBooksForAuthor_ShouldReturnEmptyList_WhenAuthorIdHasNoBooks(){
        Long authorId =1L;
        List<BookAuthorResponseDTO> mockedList = List.of();

        when(bookRepository.getBooksByAuthorId(authorId)).thenReturn(mockedList);

        List<BookAuthorResponseDTO> real = bookService.getBooksForAuthor(authorId);

        assertThat(real).isEqualTo(mockedList);

        verify(bookRepository, times(1)).getBooksByAuthorId(authorId);
    }
}
