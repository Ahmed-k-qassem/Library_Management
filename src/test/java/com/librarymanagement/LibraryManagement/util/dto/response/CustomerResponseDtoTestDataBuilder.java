package com.librarymanagement.LibraryManagement.util.dto.response;

import com.librarymanagement.LibraryManagement.dto.Response.CustomerResponseDTO;

public class CustomerResponseDtoTestDataBuilder {

    private long customerId = 1L;
    private String name = "Ahmed Qassem Default Name";
    private String userUuid = "b7f1c2d4-1111-4aaa-9000-000000000001";

    private CustomerResponseDtoTestDataBuilder() {
    }

    public static CustomerResponseDtoTestDataBuilder getInstance() {
        return new CustomerResponseDtoTestDataBuilder();
    }

    public CustomerResponseDtoTestDataBuilder withCustomerId(long customerId) {
        this.customerId = customerId;
        return this;
    }

    public CustomerResponseDtoTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerResponseDtoTestDataBuilder withUserUuid(String userUuid) {
        this.userUuid = userUuid;
        return this;
    }

    public CustomerResponseDTO build() {
        return new CustomerResponseDTO(customerId, name, userUuid);
    }
}