package com.librarymanagment.LibraryManagment.service;

import com.librarymanagment.LibraryManagment.entity.Author;
import com.librarymanagment.LibraryManagment.entity.Book;
import com.librarymanagment.LibraryManagment.entity.Category;
import com.librarymanagment.LibraryManagment.entity.Status;
import com.librarymanagment.LibraryManagment.repository.BookRepository;
import com.librarymanagment.LibraryManagment.dto.Response.BookAuthorResponseDTO;
import com.librarymanagment.LibraryManagment.dto.Request.BookRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.BookResponseDTO;
import com.librarymanagment.LibraryManagment.exception.BookNotAvailableException;
import com.librarymanagment.LibraryManagment.util.mapper.BookMapper;
import jakarta.persistence.EntityNotFoundException;
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
