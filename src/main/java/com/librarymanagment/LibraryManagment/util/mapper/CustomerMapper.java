package com.librarymanagment.LibraryManagment.util.mapper;

import com.librarymanagment.LibraryManagment.Entities.Customer;
import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.dto.Request.CustomerRequestDTO;
import com.librarymanagment.LibraryManagment.dto.Response.CustomerResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponseDTO mapCustomerToResponseDTO(Customer customer, long userId) {
        return new CustomerResponseDTO(customer.getId(), customer.getName(), userId);
    }


    public Customer mapRequestDTOtoCustomer(CustomerRequestDTO customerRequestDTO, User user) {
        return new Customer(customerRequestDTO.name(), user);
    }
}
