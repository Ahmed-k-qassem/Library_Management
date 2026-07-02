package com.librarymanagment.LibraryManagment.Controllers;

import com.librarymanagment.LibraryManagment.Entities.Book;
import com.librarymanagment.LibraryManagment.Services.AuthorService;
import com.librarymanagment.LibraryManagment.Services.BookService;
import com.librarymanagment.LibraryManagment.Services.CategoryService;
import com.librarymanagment.LibraryManagment.dto.Response.BookAuthorDTO;
import com.librarymanagment.LibraryManagment.dto.Request.BookRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.BookResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final BookService bookService;
    public BookController(AuthorService authorService, CategoryService categoryService, BookService bookService) {
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.bookService = bookService;
    }


    @GetMapping
    public List<BookResponseDTO> getBooks(){
        return bookService.getAllBooks()
                .stream()
                .map(bookService::castToBookResponseDTO)
                .toList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable long id){
        BookResponseDTO responseDTO = bookService.castToBookResponseDTO(bookService.getBookById(id));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/authors/{authorId}")
    public List<BookAuthorDTO> getBooksForAuthor(@PathVariable long authorId){
        return bookService.getBooksForAuthor(authorId);
    }


    @PostMapping
    public ResponseEntity<BookResponseDTO> addBook(@Valid @RequestBody BookRequestDTO book){
        Book createdBook = bookService.createBook(book);
        BookResponseDTO responseDTO = bookService.castToBookResponseDTO(createdBook);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
