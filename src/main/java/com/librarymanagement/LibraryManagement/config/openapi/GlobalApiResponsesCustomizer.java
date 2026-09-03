package com.librarymanagement.LibraryManagement.config.openapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class GlobalApiResponsesCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses responses = operation.getResponses();

        addIfAbsent(responses, "401",
                "No bearer token, or the token is expired / not issued by the configured realm. "
                        + "The response has no body; check the WWW-Authenticate header.");

        addIfAbsent(responses, "403",
                "The token is valid but does not carry the realm role this endpoint requires. "
                        + "The response has no body.");

        return operation;
    }

    private void addIfAbsent(ApiResponses responses, String statusCode, String description) {
        if (responses.containsKey(statusCode)) {
            return;
        }
        responses.addApiResponse(statusCode, new ApiResponse().description(description));
    }
}