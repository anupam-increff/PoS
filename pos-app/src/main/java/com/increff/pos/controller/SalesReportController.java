package com.increff.pos.controller;

import com.increff.pos.dto.SalesReportDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Sales Reports")
@RestController
@RequestMapping("/api/report/sales")
@PreAuthorize("hasAuthority('supervisor')")
public class SalesReportController {

    @Autowired
    private SalesReportDto dto;

    @ApiOperation("Get sales report with filters")
    @PostMapping("/search")
    public PaginatedResponse<SalesReportData> getSalesReport(
            @RequestBody @Valid SalesReportFilterForm form,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return dto.get(form, page, size);
    }
}
