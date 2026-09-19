package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Request.BorrowRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import com.librarymanagement.LibraryManagement.dto.Response.PageResponse;
import com.librarymanagement.LibraryManagement.service.BorrowService;
import com.librarymanagement.LibraryManagement.util.SortValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/borrow")
@Tag(name = "Borrowings")
public class BorrowController {

    private final BorrowService borrowService;

    private static final Set<String> SORTABLE = Set.of("borrowDate");

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "borrowDate");

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    @Operation(
            summary = "List borrowing records, one page at a time",
            description = """
                    Paged and sorted. `size` is capped at 100. Sortable field: `borrowDate`;
                    any other sort field returns 400. Defaults to page 0, size 20, newest first.
                    Requires the **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "One page of borrowing records. A page past the end has empty `content`."),
            @ApiResponse(
                    responseCode = "400",
                    description = "The sort field is not one of the allowed values",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public PageResponse<BorrowResponseDTO> getAllBorrowings(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(borrowService.getAllBorrows(withSafeSort(pageable)));
    }

    private static Pageable withSafeSort(Pageable requested) {
        Sort sort = SortValidator.validate(requested.getSort(), SORTABLE, DEFAULT_SORT);
        return PageRequest.of(requested.getPageNumber(), requested.getPageSize(),
                sort.and(Sort.by(Sort.Direction.DESC, "id")));
    }

    @PostMapping
    @Operation(
            summary = "Borrow a book",
            description = """
                    Records that a customer took a book out and flips the book's status to `BORROWED`.
                    The book must currently be `AVAILABLE`.

                    Requires the **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "The borrowing was recorded",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BorrowResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book or no customer exists with the given id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class))),
            @ApiResponse(
                    responseCode = "503",
                    description = """
                            The book exists but is not currently available (already BORROWED, LOST
                            or in MAINTENANCE). Raised as BookNotAvailableException.""",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "The book is not available for borrowing",
                                      "statusCode": 503
                                    }""")))
    })
    public ResponseEntity<BorrowResponseDTO> createBorrow(@RequestBody BorrowRequestDTO borrowRequestDTO) {
        return new ResponseEntity<>(borrowService.borrowBook(borrowRequestDTO), HttpStatus.CREATED);
    }
}