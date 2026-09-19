package com.librarymanagement.LibraryManagement.util;

import com.librarymanagement.LibraryManagement.exception.InvalidSortException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortValidatorTest {

    private static final Set<String> ALLOWED = Set.of("title", "addedDate");
    private static final Sort FALLBACK = Sort.by("title");

    @Test
    @DisplayName("No sort requested: the endpoint's fallback is used")
    void givenUnsorted_whenValidate_thenReturnsFallback() {
        assertThat(SortValidator.validate(Sort.unsorted(), ALLOWED, FALLBACK))
                .isEqualTo(FALLBACK);
    }

    @Test
    @DisplayName("Allowed field: returned unchanged, direction preserved")
    void givenAllowedProperty_whenValidate_thenReturnsRequestedUnchanged() {
        Sort requested = Sort.by(Sort.Direction.DESC, "addedDate");

        assertThat(SortValidator.validate(requested, ALLOWED, FALLBACK)).isEqualTo(requested);
    }

    @Test
    @DisplayName("Several allowed fields: all accepted")
    void givenSeveralAllowedProperties_whenValidate_thenAccepted() {
        Sort requested = Sort.by("title").and(Sort.by(Sort.Direction.DESC, "addedDate"));

        assertThat(SortValidator.validate(requested, ALLOWED, FALLBACK)).isEqualTo(requested);
    }

    @Test
    @DisplayName("One bad field among good ones: the whole sort is rejected")
    void givenOneBadPropertyAmongGoodOnes_whenValidate_thenRejectsWholeSort() {
        Sort requested = Sort.by("title").and(Sort.by("author.nationality"));

        assertThatThrownBy(() -> SortValidator.validate(requested, ALLOWED, FALLBACK))
                .isInstanceOf(InvalidSortException.class);
    }

    @Test
    @DisplayName("Association path: rejected, so no unplanned join reaches the database")
    void givenAssociationPath_whenValidate_thenRejected() {
        assertThatThrownBy(() -> SortValidator.validate(Sort.by("user.role"), ALLOWED, FALLBACK))
                .isInstanceOf(InvalidSortException.class);
    }

    @Test
    @DisplayName("Rejection message lists the allowed fields alphabetically")
    void givenBadProperty_whenValidate_thenMessageListsAllowedFieldsSorted() {
        assertThatThrownBy(() -> SortValidator.validate(Sort.by("password"), ALLOWED, FALLBACK))
                .isInstanceOf(InvalidSortException.class)
                .hasMessage("Invalid sort property. Allowed values: addedDate, title");
    }

    @Test
    @DisplayName("Rejection message never echoes the rejected value back")
    void givenBadProperty_whenValidate_thenMessageDoesNotEchoInput() {
        assertThatThrownBy(() -> SortValidator.validate(Sort.by("password"), ALLOWED, FALLBACK))
                .message()
                .doesNotContain("password");
    }
}