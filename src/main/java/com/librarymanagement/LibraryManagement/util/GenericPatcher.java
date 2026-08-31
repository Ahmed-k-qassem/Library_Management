package com.librarymanagement.LibraryManagement.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import com.librarymanagement.LibraryManagement.exception.JsonPatchProcessingException;
import org.springframework.stereotype.Component;

@Component
public class GenericPatcher {
    private final ObjectMapper objectMapper;

    public GenericPatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T applyPatch(String patchBody, T targetDto, Class<T> clazz) {
        try {
            JsonNode patchNode = objectMapper.readTree(patchBody);
            JsonNode targetNode = objectMapper.convertValue(targetDto, JsonNode.class);

            JsonNode patchedNode = JsonPatch.apply(patchNode, targetNode);

            return objectMapper.treeToValue(patchedNode, clazz);
        } catch (JsonProcessingException e) {
            throw new JsonPatchProcessingException("Failed to apply patch: " + e.getMessage());
        }
    }
}
