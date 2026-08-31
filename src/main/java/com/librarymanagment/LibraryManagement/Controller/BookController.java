package com.librarymanagment.LibraryManagement.Controller;

import com.librarymanagment.LibraryManagement.service.BookService;
import com.librarymanagment.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import com.librarymanagment.LibraryManagement.dto.Request.BookRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.BookResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping
    public List<BookResponseDTO> getBooks(){
        return bookService.findAllBooks();
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable long id){
        BookResponseDTO responseDTO = bookService.getBookResponseById(id);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/author/{authorId}")
    public List<BookAuthorResponseDTO> getBooksForAuthor(@PathVariable long authorId){
        return bookService.getBooksForAuthor(authorId);
    }


    @PostMapping
    public ResponseEntity<BookResponseDTO> addBook(@Valid @RequestBody BookRequestDTO book){
        BookResponseDTO responseDTO = bookService.createBook(book);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
