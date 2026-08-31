package com.librarymanagement.LibraryManagement.util.mapper;

import com.librarymanagement.LibraryManagement.entity.Customer;
import com.librarymanagement.LibraryManagement.entity.User;
import com.librarymanagement.LibraryManagement.dto.Request.CustomerRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.CustomerResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponseDTO mapCustomerToResponseDTO(Customer customer, String user_uuid) {
        return new CustomerResponseDTO(customer.getId(), customer.getName(), user_uuid);
    }


    public Customer mapRequestDTOtoCustomer(CustomerRequestDTO customerRequestDTO, User user) {
        return new Customer(customerRequestDTO.name(), user);
    }
}
