package com.increff.pos.util;

import com.increff.pos.model.data.PaginatedResponse;
import java.util.List;

public class PaginationUtil {

    private PaginationUtil() {
        // Private constructor to prevent instantiation
    }

    public static <T> PaginatedResponse<T> createPaginatedResponse(List<T> data, Integer page, Integer pageSize, Long totalItems) {
        Integer totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return new PaginatedResponse<>(data, page, totalPages, totalItems, pageSize);
    }
} 