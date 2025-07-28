package com.increff.pos.service;

import com.increff.pos.dao.SalesReportDao;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SalesReportService {

    @Autowired
    private SalesReportDao salesReportDao;

    public PaginatedResponse<SalesReportData> getSalesReport(
            ZonedDateTime start, ZonedDateTime end, String clientName, Integer page, Integer size) {

        List<SalesReportData> content = salesReportDao.getSalesReport(start, end, clientName, page, size);
        Long totalItems = salesReportDao.countTotalClients(start, end, clientName);
        Integer totalPages = (int) Math.ceil((double) totalItems / size);

        return new PaginatedResponse<>(content, page, totalPages, totalItems, size);
    }
}
