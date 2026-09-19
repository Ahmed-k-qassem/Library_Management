package com.librarymanagement.LibraryManagement.service;

import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import com.librarymanagement.LibraryManagement.repository.BorrowRepository;
import com.librarymanagement.LibraryManagement.util.dto.response.BorrowResponseDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.mapper.BorrowMapper;
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
class BorrowServiceTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private BookService bookService;

    @Mock
    private BorrowMapper borrowMapper;

    @InjectMocks
    private BorrowService borrowService;

    @Test
    @DisplayName("getAllBorrows forwards the pageable unchanged and keeps the page metadata")
    void givenPageable_whenGetAllBorrows_thenForwardsItUnchangedAndKeepsMetadata() {
        Pageable pageable = PageRequest.of(3, 5,
                Sort.by(Sort.Direction.DESC, "borrowDate").and(Sort.by(Sort.Direction.DESC, "id")));
        Page<BorrowResponseDTO> stubbed = new PageImpl<>(
                List.of(BorrowResponseDtoTestDataBuilder.getInstance().withBookTitle("Dune").build()),
                pageable, 16);
        when(borrowRepository.findAllSummaries(pageable)).thenReturn(stubbed);

        Page<BorrowResponseDTO> result = borrowService.getAllBorrows(pageable);

        assertThat(result.getContent()).extracting(BorrowResponseDTO::bookTitle).containsExactly("Dune");
        assertThat(result.getTotalElements()).isEqualTo(16);
        assertThat(result.getNumber()).isEqualTo(3);
        assertThat(result.getSize()).isEqualTo(5);
        verify(borrowRepository).findAllSummaries(pageable);
    }

    @Test
    @DisplayName("getAllBorrows no longer loads entities or maps them: the projection builds the DTO")
    void givenPageable_whenGetAllBorrows_thenNoEntityLoadAndNoMapper() {
        Pageable pageable = PageRequest.of(0, 20);
        when(borrowRepository.findAllSummaries(pageable)).thenReturn(Page.empty(pageable));

        borrowService.getAllBorrows(pageable);

        verify(borrowRepository, never()).findAll();
        verify(borrowRepository, never()).findAll(any(Pageable.class));
        verifyNoInteractions(borrowMapper);
    }

    @Test
    @DisplayName("getAllBorrows returns an empty page, not null, when there are no borrowings")
    void givenNoBorrowings_whenGetAllBorrows_thenEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(borrowRepository.findAllSummaries(pageable)).thenReturn(Page.empty(pageable));

        Page<BorrowResponseDTO> result = borrowService.getAllBorrows(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}