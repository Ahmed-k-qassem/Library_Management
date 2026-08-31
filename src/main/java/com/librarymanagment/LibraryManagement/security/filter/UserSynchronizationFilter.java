package com.librarymanagment.LibraryManagement.security.filter;

import com.librarymanagment.LibraryManagement.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserSynchronizationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public UserSynchronizationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();


            String uuid = jwt.getSubject();


            String username = jwt.getClaimAsString("preferred_username");

            String role = extractSingleRole(jwt);

            userService.syncUser(uuid, username, role);
        }

        filterChain.doFilter(request, response);
    }

    private String extractSingleRole(Jwt jwt) {
        String role = "USER";
        if (jwt.hasClaim("realm_access")) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                role = extractRoleFromList(roles);
            }
        }
        return role;
    }


    private String extractRoleFromList(List<String> roles){
        if (roles != null && !roles.isEmpty()) {
            return roles.stream()
                    .filter(role -> !role.startsWith("default-roles-"))
                    .findFirst()
                    .orElse("USER");
        }
        return "USER";
    }
}