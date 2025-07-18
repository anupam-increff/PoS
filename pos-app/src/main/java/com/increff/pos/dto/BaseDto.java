package com.increff.pos.dto;

import com.increff.pos.model.data.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseDto {
    
    protected <T> PaginatedResponse<T> createPaginatedResponse(List<T> data, int page, int pageSize, long totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return new PaginatedResponse<>(data, page, totalPages, totalItems, pageSize);
    }
} 