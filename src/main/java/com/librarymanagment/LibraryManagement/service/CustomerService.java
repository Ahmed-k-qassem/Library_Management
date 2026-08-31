package com.librarymanagment.LibraryManagement.service;

import com.librarymanagment.LibraryManagement.entity.Customer;
import com.librarymanagment.LibraryManagement.entity.User;
import com.librarymanagment.LibraryManagement.repository.CustomerRepository;
import com.librarymanagment.LibraryManagement.dto.Request.CustomerRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.CustomerResponseDTO;
import com.librarymanagment.LibraryManagement.util.mapper.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final CustomerMapper customerMapper;
    public CustomerService(CustomerRepository customerRepository,UserService userService, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.userService = userService;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO, String keyCloakUserid) {
        User user = userService.findByKeycloakUserId(keyCloakUserid);
        Customer customer = customerMapper.mapRequestDTOtoCustomer(customerRequestDTO, user);
        return customerMapper.mapCustomerToResponseDTO(customerRepository.save(customer), user.getKeycloakUserId());
    }



    @Transactional
    protected Customer getCustomerById(long id) {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }


    @Transactional
    public CustomerResponseDTO getCustomerResponseById(long id, String username) {
        User user = userService.findByUsername(username);
        return customerMapper.mapCustomerToResponseDTO(getCustomerById(id), user.getKeycloakUserId());
    }


    @Transactional
    public List<CustomerResponseDTO> getAllCustomers(){
        return customerRepository.findAll()
                .stream()
                .map(customer -> customerMapper.mapCustomerToResponseDTO(customer, customer.getUser().getKeycloakUserId()))
                .toList();
    }
}
