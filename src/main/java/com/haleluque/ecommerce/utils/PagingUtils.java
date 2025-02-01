package com.haleluque.ecommerce.utils;

import org.springframework.data.domain.Sort;

public class PagingUtils {
    public static Sort getSortByAndOrder(String sortBy, String sortOrder) {
        return sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }
}
