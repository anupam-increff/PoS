package com.increff.pos.controller;

import com.increff.pos.dto.DaySalesDto;
import com.increff.pos.pojo.DaySalesPojo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "Day Sales Reports")
@RestController
@RequestMapping("/api/reports/day-sales")
@PreAuthorize("hasAuthority('supervisor')")
public class DaySalesController {

    @Autowired
    private DaySalesDto dto;

    @ApiOperation("Get sales data for date range")
    @GetMapping
    public List<DaySalesPojo> getSales(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        LocalDate startDate = LocalDate.parse(start.trim());
        LocalDate endDate = LocalDate.parse(end.trim());
        return dto.getByDateRange(startDate, endDate);
    }
    
    @ApiOperation("Generate today's sales report")
    @PostMapping("/generate")
    public void generateTodayReport() {
        dto.generateTodayReport();
    }
}
