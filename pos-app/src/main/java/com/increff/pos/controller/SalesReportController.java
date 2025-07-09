package com.increff.pos.controller;

import com.increff.pos.dto.SalesReportDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/report")
public class SalesReportController {

    @Autowired
    private SalesReportDto salesReportDto;

    @ApiOperation("Get sales report with filters")
    @PostMapping("/sales")
    public PaginatedResponse<SalesReportData> getSalesReport(
            @RequestBody @Valid SalesReportFilterForm form,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return salesReportDto.get(form, page, size);
    }
}
