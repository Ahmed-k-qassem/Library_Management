package com.librarymanagement.LibraryManagement.controller;

import com.librarymanagement.LibraryManagement.Controller.CustomerController;
import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

@WebMvcTest(CustomerController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
public class CustomerControllerTest {

}
