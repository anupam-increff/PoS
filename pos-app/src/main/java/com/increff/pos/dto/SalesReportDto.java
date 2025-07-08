package com.increff.pos.dto;

import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import com.increff.pos.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
public class SalesReportDto {

    @Autowired
    private SalesReportService salesReportService;

    public PaginatedResponse<SalesReportData> get(@Valid SalesReportFilterForm form, int page, int size) {
        return salesReportService.getSalesReport(form.getStartDate(), form.getEndDate(), form.getClientName(), page, size);
    }
}
