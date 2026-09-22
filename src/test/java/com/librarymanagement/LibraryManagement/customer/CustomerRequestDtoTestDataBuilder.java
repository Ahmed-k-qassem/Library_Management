package com.librarymanagement.LibraryManagement.customer;

class CustomerRequestDtoTestDataBuilder {

    private String name = "Ahmed Qassem Default Name";

    private CustomerRequestDtoTestDataBuilder() {
    }

    public static CustomerRequestDtoTestDataBuilder getInstance() {
        return new CustomerRequestDtoTestDataBuilder();
    }

    public CustomerRequestDtoTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerRequestDTO build() {
        return new CustomerRequestDTO(name);
    }
}