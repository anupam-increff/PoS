package com.increff.pos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaginationConfig {
    
    @Value("${app.pagination.default-page:0}")
    private int defaultPage;
    
    @Value("${app.pagination.default-page-size:5}")
    private int defaultPageSize;
    
    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;
    
    public int getDefaultPage() {
        return defaultPage;
    }
    
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    
    public int getMaxPageSize() {
        return maxPageSize;
    }
    
    public int validatePageSize(int pageSize) {
        return Math.min(pageSize, maxPageSize);
    }
} 