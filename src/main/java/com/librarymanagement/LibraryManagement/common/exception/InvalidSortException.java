package com.librarymanagement.LibraryManagement.common.exception;

import java.util.Set;
import java.util.TreeSet;

public class InvalidSortException extends RuntimeException {

    private final Set<String> allowedProperties;

    public InvalidSortException(Set<String> allowedProperties) {
        super("Invalid sort property. Allowed values: "
                + String.join(", ", new TreeSet<>(allowedProperties)));
        this.allowedProperties = Set.copyOf(allowedProperties);
    }

    public Set<String> getAllowedProperties() {
        return allowedProperties;
    }
}