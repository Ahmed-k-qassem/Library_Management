package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Request.CustomerRequestDTO;
import com.librarymanagement.LibraryManagement.dto.Response.CustomerResponseDTO;
import com.librarymanagement.LibraryManagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(
            summary = "List all customers",
            description = "Requires the **USER** or **ADMIN** realm role.")
    @ApiResponse(
            responseCode = "200",
            description = "All customers, possibly empty",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = CustomerResponseDTO.class))))
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping
    @Operation(
            summary = "Register the caller as a library customer",
            description = """
                    The customer is linked to the `sub` claim of the bearer token, so the caller
                    can only ever create a customer row for themselves — there is no field in the
                    payload for choosing a different user.

                    Requires the **USER** or **ADMIN** realm role.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "The customer was created and linked to the caller's Keycloak subject",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "The name is shorter than 20 characters, or this user already has a customer row",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "The minimum size of the costumer name should be at least 20"
                                    }""")))
    })
    public ResponseEntity<CustomerResponseDTO> saveCustomer(
            @RequestBody CustomerRequestDTO customerRequestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {

        return new ResponseEntity<>(
                customerService.createCustomer(customerRequestDTO, jwt.getSubject()),
                HttpStatus.CREATED);
    }
}