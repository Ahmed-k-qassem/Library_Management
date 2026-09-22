package com.librarymanagement.LibraryManagement.common;

import com.librarymanagement.LibraryManagement.common.dto.Response.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResponseTest {

    @Test
    @DisplayName("Middle page: every field is copied (distinct values, so a swap would show)")
    void givenMiddlePage_whenFrom_thenCopiesEveryField() {
        Page<String> page = new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 2), 5);

        PageResponse<String> response = PageResponse.from(page);

        assertThat(response.content()).containsExactly("a", "b");
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
    }

    @Test
    @DisplayName("Last, partially filled page: size is what was requested, not what came back")
    void givenPartialLastPage_whenFrom_thenSizeIsRequestedSize() {
        Page<String> page = new PageImpl<>(List.of("e"), PageRequest.of(2, 2), 5);

        PageResponse<String> response = PageResponse.from(page);

        assertThat(response.content()).hasSize(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.last()).isTrue();
    }

    @Test
    @DisplayName("Empty result: zero totals, and the page counts as both first and last")
    void givenEmptyPage_whenFrom_thenZeroTotals() {
        PageResponse<String> response = PageResponse.from(Page.empty(PageRequest.of(0, 20)));

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();
    }
}