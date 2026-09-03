package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Request.BorrowRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import com.librarymanagement.LibraryManagement.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@Tag(name = "Borrowings")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    @Operation(
            summary = "List every borrowing record",
            description = "Requires the **ADMIN** realm role.")
    @ApiResponse(
            responseCode = "200",
            description = "All borrowing records, possibly empty",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = BorrowResponseDTO.class))))
    public List<BorrowResponseDTO> getAllBorrowings() {
        return borrowService.getAllBorrows();
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