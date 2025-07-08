package com.increff.pos.controller;

import com.increff.pos.dto.SalesReportDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/report/sales")
public class SalesReportController {

    @Autowired
    private SalesReportDto dto;

    @PostMapping("/search")
    public PaginatedResponse<SalesReportData> getSalesReport(
            @RequestBody @Valid SalesReportFilterForm form,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return dto.get(form, page, size);
    }
}
