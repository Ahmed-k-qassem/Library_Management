package com.librarymanagement.LibraryManagement.customer;

import com.librarymanagement.LibraryManagement.user.User;
import org.springframework.stereotype.Component;

@Component
class CustomerMapper {
    public CustomerResponseDTO mapCustomerToResponseDTO(Customer customer, String user_uuid) {
        return new CustomerResponseDTO(customer.getId(), customer.getName(), user_uuid);
    }


    public Customer mapRequestDTOtoCustomer(CustomerRequestDTO customerRequestDTO, User user) {
        return new Customer(customerRequestDTO.name(), user);
    }
}
