package com.librarymanagement.LibraryManagement.common.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
@Schema(description = "Page response for paginitaion result")
public record PageResponse<T>(
        @Schema(description = "Content that will be in the list, generally the object which will be displayed (authors,books etc..)")
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
){
    public static <T> PageResponse<T> from(Page<T> page){
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(),
                page.isFirst(), page.isLast()
        );
    }
}
