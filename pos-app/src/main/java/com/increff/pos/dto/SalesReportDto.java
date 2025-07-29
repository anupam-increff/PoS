package com.increff.pos.dto;

import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import com.increff.pos.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SalesReportDto extends AbstractDto {

    @Autowired
    private SalesReportService salesReportService;

    public PaginatedResponse<SalesReportData> get(SalesReportFilterForm form, int page, int size) {
        checkValid(form);
        return salesReportService.getSalesReport(form.getStartDate(), form.getEndDate(), form.getClientName(), page, size);
    }
}