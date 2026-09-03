package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Request.BookRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BookResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import com.librarymanagement.LibraryManagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(
            summary = "List all books",
            description = "Requires the **USER** realm role.")
    @ApiResponse(
            responseCode = "200",
            description = "The full catalogue, possibly empty",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = BookResponseDTO.class))))
    public List<BookResponseDTO> getBooks() {
        return bookService.findAllBooks();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get one book by id",
            description = "Requires the **USER** realm role.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The book was found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<BookResponseDTO> getBookById(
            @Parameter(description = "Database id of the book", example = "1", required = true)
            @PathVariable long id) {
        BookResponseDTO responseDTO = bookService.getBookResponseById(id);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/author/{authorId}")
    @Operation(
            summary = "List the books written by one author",
            description = """
                    Returns a slimmer projection than the other book endpoints: no id, no
                    category, no added date. Requires the **USER** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The author's books, possibly empty",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BookAuthorResponseDTO.class)))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No author exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public List<BookAuthorResponseDTO> getBooksForAuthor(
            @Parameter(description = "Database id of the author", example = "1", required = true)
            @PathVariable long authorId) {
        return bookService.getBooksForAuthor(authorId);
    }

    @PostMapping
    @Operation(
            summary = "Add a book to the catalogue",
            description = """
                    `authorId` and `categoryId` must reference rows that already exist.
                    Requires the **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "The book was created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bean-validation failed. The body is a flat map of field name to message.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object", description = "field name -> validation message"),
                            examples = @ExampleObject(value = """
                                    {
                                      "title": "Book title must be 10 or above in characters",
                                      "authorId": "Author ID is required"
                                    }"""))),
            @ApiResponse(
                    responseCode = "404",
                    description = "The referenced author or category does not exist",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<BookResponseDTO> addBook(@Valid @RequestBody BookRequestDTO book) {
        BookResponseDTO responseDTO = bookService.createBook(book);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remove a book from the catalogue",
            description = "Requires the **ADMIN** realm role.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The book was deleted; no body is returned"),
            @ApiResponse(
                    responseCode = "400",
                    description = "The book cannot be deleted because borrowing rows still reference it",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book exists with that id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Database id of the book to delete", example = "1", required = true)
            @PathVariable long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}