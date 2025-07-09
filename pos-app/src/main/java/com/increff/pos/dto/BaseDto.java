package com.increff.pos.dto;

import com.increff.pos.config.PaginationConfig;
import com.increff.pos.model.data.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseDto {
    
    @Autowired
    protected PaginationConfig paginationConfig;
    
    protected <T> PaginatedResponse<T> createPaginatedResponse(List<T> data, int page, int pageSize, long totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return new PaginatedResponse<>(data, page, totalPages, totalItems, pageSize);
    }
    
    protected int getDefaultPage() {
        return paginationConfig.getDefaultPage();
    }
    
    protected int getDefaultPageSize() {
        return paginationConfig.getDefaultPageSize();
    }
    
    protected int validatePageSize(int pageSize) {
        return paginationConfig.validatePageSize(pageSize);
    }
} 