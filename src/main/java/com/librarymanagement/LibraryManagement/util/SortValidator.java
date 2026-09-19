package com.librarymanagement.LibraryManagement.util;

import com.librarymanagement.LibraryManagement.exception.InvalidSortException;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SortValidator {
    public static Sort validate(Sort requested, Set<String> allowed, Sort fallback){
        if(requested.isUnsorted()) return fallback;
        boolean ok = requested.stream()
                .allMatch(o -> allowed.contains(o.getProperty()));
        if(!ok) throw new InvalidSortException(allowed);
        return requested;
    }
}
