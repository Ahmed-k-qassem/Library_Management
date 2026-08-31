package com.librarymanagment.LibraryManagement.util.mapper;

import com.librarymanagment.LibraryManagement.entity.Customer;
import com.librarymanagment.LibraryManagement.entity.User;
import com.librarymanagment.LibraryManagement.dto.Request.CustomerRequestDTO;
import com.librarymanagment.LibraryManagement.dto.Response.CustomerResponseDTO;
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
