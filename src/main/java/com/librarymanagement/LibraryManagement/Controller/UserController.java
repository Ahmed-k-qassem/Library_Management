package com.librarymanagement.LibraryManagement.Controller;

import com.librarymanagement.LibraryManagement.dto.Response.HttpDTO;
import com.librarymanagement.LibraryManagement.dto.Response.UserResponseDTO;
import com.librarymanagement.LibraryManagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "List the synchronised users",
            description = """
                    Users are mirrored into the local database by `UserSynchronizationFilter` the
                    first time each token is seen, so this list only contains people who have
                    actually called the API — it is not the full Keycloak realm.

                    Currently open to **any authenticated user**.""")
    @ApiResponse(
            responseCode = "200",
            description = "All synchronised users, possibly empty",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
    public List<UserResponseDTO> getUsers() {
        return userService.findAll();
    }

    @DeleteMapping("admin/{uuid}")
    @Operation(
            summary = "Delete the local mirror of a user",
            description = """
                    Removes the local row only. The account still exists in Keycloak, and it will
                    be recreated here on that user's next request.

                    Currently open to **any authenticated user**.""")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The local user row was deleted; no body is returned"),
            @ApiResponse(
                    responseCode = "400",
                    description = "No local user row exists for that subject",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HttpDTO.class)))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    description = "The Keycloak subject (`sub` claim) of the user, i.e. the realm UUID",
                    example = "9f6c1e2a-4b7d-4f1e-9c33-0a2b8d5e7f11",
                    required = true)
            @PathVariable String uuid) {
        userService.deleteUserByKeycloakId(uuid);
        return ResponseEntity.noContent().build();
    }
}