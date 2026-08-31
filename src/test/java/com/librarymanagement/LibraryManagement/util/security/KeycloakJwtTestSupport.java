package com.librarymanagement.LibraryManagement.util.security;

import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class KeycloakJwtTestSupport {
    public static final String PATRON_UUID = "b7f1c2d4-1111-4aaa-9000-000000000001";
    public static final String PATRON_USERNAME = "ahmed";

    public static final String ADMIN_UUID = "b7f1c2d4-2222-4bbb-9000-000000000002";
    public static final String ADMIN_USERNAME = "layla";

    public static final String OUTSIDER_UUID = "b7f1c2d4-3333-4ccc-9000-000000000003";
    public static final String OUTSIDER_USERNAME = "stranger";

    private KeycloakJwtTestSupport() {
    }

    public static RequestPostProcessor patron() {
        return keycloakUser(PATRON_UUID, PATRON_USERNAME, "default-roles-library", "USER");
    }

    public static RequestPostProcessor admin() {
        return keycloakUser(ADMIN_UUID, ADMIN_USERNAME, "default-roles-library", "ADMIN");
    }

    public static RequestPostProcessor outsider() {
        return keycloakUser(OUTSIDER_UUID, OUTSIDER_USERNAME, "default-roles-library");
    }

    public static RequestPostProcessor withoutRealmAccessClaim(String uuid) {
        return jwt()
                .jwt(token -> token.claim("sub", uuid))
                .authorities(new KeycloakRoleConverter());
    }

    public static RequestPostProcessor withEmptyRoles(String uuid) {
        return jwt()
                .jwt(token -> token
                        .claim("sub", uuid)
                        .claim("realm_access", Map.of("roles", List.of())))
                .authorities(new KeycloakRoleConverter());
    }

    public static RequestPostProcessor keycloakUser(String sub, String username, String... realmRoles) {
        return jwt()
                .jwt(token -> token
                        .claim("sub", sub)
                        .claim("preferred_username", username)
                        .claim("realm_access", Map.of("roles", List.of(realmRoles))))
                .authorities(new KeycloakRoleConverter());
    }

}
