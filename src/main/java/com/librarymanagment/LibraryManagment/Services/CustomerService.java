package com.librarymanagment.LibraryManagment.Services;

import com.librarymanagment.LibraryManagment.Entities.Customer;
import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.Repostries.CustomerRepository;
import com.librarymanagment.LibraryManagment.dto.Request.CustomerRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.CustomerResponseDTO;
import com.librarymanagment.LibraryManagment.util.mapper.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO, String username) {
        User user = userService.findByUsername(username);
        Customer customer = customerMapper.mapRequestDTOtoCustomer(customerRequestDTO, user);
        return customerMapper.mapCustomerToResponseDTO(customerRepository.save(customer), user.getId());
    }



    @Transactional
    protected Customer getCustomerById(long id) {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }


    @Transactional
    public CustomerResponseDTO getCustomerResponseById(long id, String username) {
        User user = userService.findByUsername(username);
        return customerMapper.mapCustomerToResponseDTO(getCustomerById(id), user.getId());
    }


    @Transactional
    public List<CustomerResponseDTO> getAllCustomers(){
        return customerRepository.findAll()
                .stream()
                .map(customer -> customerMapper.mapCustomerToResponseDTO(customer, customer.getUser().getId()))
                .toList();
    }
}
