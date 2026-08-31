package com.librarymanagement.LibraryManagement.service;

import com.librarymanagement.LibraryManagement.entity.Author;
import com.librarymanagement.LibraryManagement.entity.Book;
import com.librarymanagement.LibraryManagement.entity.Category;
import com.librarymanagement.LibraryManagement.entity.Status;
import com.librarymanagement.LibraryManagement.repository.BookRepository;
import com.librarymanagement.LibraryManagement.dto.Response.BookAuthorResponseDTO;
import com.librarymanagement.LibraryManagement.dto.Request.BookRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.BookResponseDTO;
import com.librarymanagement.LibraryManagement.exception.BookNotAvailableException;
import com.librarymanagement.LibraryManagement.util.mapper.BookMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final BookMapper bookMapper;
    public BookService(BookRepository bookRepository, AuthorService authorService, CategoryService categoryService,  BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.bookMapper = bookMapper;
    }



    @Transactional
    public List<BookAuthorResponseDTO> getBooksForAuthor(long id){
        return bookRepository.getBooksByAuthorId(id);
    }

    @Transactional
    protected Book getBookById(long id){
        return bookRepository.getBookById(id).orElseThrow( () -> new EntityNotFoundException("Book has not been found "));
    }


    @Transactional
    public BookResponseDTO getBookResponseById(long id){
        return bookMapper.mapBookToResponseDTO(getBookById(id));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto){
        Author author = authorService.findById(dto.authorId());
        Category category = categoryService.getCategoryById(dto.categoryId());

        return bookMapper.mapBookToResponseDTO(bookRepository.save(bookMapper.mapRequestDTOtoBook(dto, author, category)));
    }


    @Transactional
    public void deleteBookById(long id){
        int rowsAffected = bookRepository.deleteBookById(id);
        if(rowsAffected == 0){
            throw new EntityNotFoundException("Book has not been found");
        }
    }


    public List<BookResponseDTO> findAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::mapBookToResponseDTO)
                .toList();
    }



    protected Book updateStatus(Book book, Status status){
        if (book.getStatus() == Status.BORROWED) {
            throw new BookNotAvailableException("Book is already borrowed");
        }
        book.setStatus(status);
        return bookRepository.save(book);
    }

}
