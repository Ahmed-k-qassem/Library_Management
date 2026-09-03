package com.librarymanagement.LibraryManagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearer-jwt";
    public static final String OAUTH2_SCHEME = "keycloak-oauth2";

    private final String issuerUri;

    public OpenApiConfig(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        this.issuerUri = issuerUri;
    }

    @Bean
    public OpenAPI libraryManagementOpenAPI() {
        return new OpenAPI()
                .info(info())
                .servers(servers())
                .tags(tags())
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, bearerScheme())
                        .addSecuritySchemes(OAUTH2_SCHEME, keycloakScheme()))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH2_SCHEME));
    }

    private Info info() {
        return new Info()
                .title("Library Management API")
                .version("v1")
                .description("""
                        REST API for a small library: authors, books, categories, customers and borrowings.

                        **Authentication** — this service is an OAuth2 *resource server*. It does not issue
                        tokens; it only validates them. Get a token from the Keycloak realm and send it as
                        `Authorization: Bearer <token>`.

                        **Authorisation** — realm roles are mapped to Spring authorities by
                        `KeycloakRoleConverter`. The role each endpoint needs is stated in its description.
                        """)
                .contact(new Contact()
                        .name("Ahmed Qassem")
                        .url("https://github.com/Ahmed-k-qassem"))
                .license(new License().name("MIT"));
    }

    private List<Server> servers() {
        return List.of(
                new Server().url("http://localhost:8081").description("Local development"),
                new Server().url("https://library.example.com").description("Production (placeholder)"));
    }

    private List<Tag> tags() {
        return List.of(
                new Tag().name("Authors").description("Authors catalogue. Reading is open to any authenticated user; writing is ADMIN only."),
                new Tag().name("Books").description("Book catalogue, including lookup by author."),
                new Tag().name("Categories").description("Book categories. Every operation is ADMIN only."),
                new Tag().name("Customers").description("Library members. A customer row is linked to the Keycloak subject that created it."),
                new Tag().name("Borrowings").description("Borrowing records. ADMIN only."),
                new Tag().name("Users").description("Users synchronised from Keycloak by UserSynchronizationFilter."));
    }

    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Paste a raw access token issued by the Keycloak realm.");
    }

    private SecurityScheme keycloakScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Log in through Keycloak directly from Swagger UI.")
                .flows(new OAuthFlows().authorizationCode(new OAuthFlow()
                        .authorizationUrl(issuerUri + "/protocol/openid-connect/auth")
                        .tokenUrl(issuerUri + "/protocol/openid-connect/token")
                        .scopes(new Scopes()
                                .addString("openid", "OpenID Connect scope")
                                .addString("profile", "Basic profile claims"))));
    }
}