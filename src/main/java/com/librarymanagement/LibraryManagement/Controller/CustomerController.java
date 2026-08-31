package com.librarymanagement.LibraryManagement.Controller;


import com.librarymanagement.LibraryManagement.service.CustomerService;
import com.librarymanagement.LibraryManagement.dto.Request.CustomerRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.CustomerResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }


    @PostMapping
    public ResponseEntity<CustomerResponseDTO> saveCustomer(@RequestBody CustomerRequestDTO customerRequestDTO, @AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(customerService.createCustomer(customerRequestDTO, jwt.getSubject()), HttpStatus.CREATED);
    }
}
