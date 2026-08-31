package com.librarymanagement.LibraryManagement.exception;

import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<HttpDTO> handelNotFound(EntityNotFoundException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(JsonPatchProcessingException.class)
    public ResponseEntity<HttpDTO> handelPatchProcess(JsonPatchProcessingException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }


        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HttpDTO> handleUsernameNotFound(UsernameNotFoundException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HttpDTO> handleDataIntegrityViolation(DataIntegrityViolationException exc){
        HttpDTO error = new HttpDTO("An error occurred while processing your data. Please review your input and try again.", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpDTO> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<HttpDTO> authorizationDeniedException(AuthorizationDeniedException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<HttpDTO> noResourceFoundException(NoResourceFoundException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<HttpDTO> bookNotAvailableException(BookNotAvailableException exc){
        HttpDTO error = new HttpDTO(exc.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
