package com.librarymanagement.LibraryManagement.book;

import com.librarymanagement.LibraryManagement.author.AuthorService;
import com.librarymanagement.LibraryManagement.author.Author;
import com.librarymanagement.LibraryManagement.category.CategoryService;
import com.librarymanagement.LibraryManagement.category.Category;
import com.librarymanagement.LibraryManagement.borrow.BookNotAvailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<BookAuthorResponseDTO> getBooksForAuthor(long id, Pageable pageable){
        return bookRepository.getBooksByAuthorId(id, pageable);
    }

    @PreAuthorize("hasRole('Admin')")
    @Transactional
    public Book getBookById(long id){
        return bookRepository.getBookById(id).orElseThrow( () -> new EntityNotFoundException("Book has not been found "));
    }


    @Transactional(readOnly = true)
    public Page<BookResponseDTO> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::mapBookToResponseDTO);
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



    public Book updateStatus(Book book, Status status){
        if (book.getStatus() == Status.BORROWED) {
            throw new BookNotAvailableException("Book is already borrowed");
        }
        book.setStatus(status);
        return bookRepository.save(book);
    }

}
