package com.increff.pos.controller;

import com.increff.pos.dto.DaySalesDto;
import com.increff.pos.model.data.DaySalesData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports/day-sales")
public class DaySalesController {

    @Autowired
    private DaySalesDto dto;

    @ApiOperation("Get sales data for date range")
    @GetMapping
    public List<DaySalesData> getSales(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        ZonedDateTime startDate = ZonedDateTime.parse(start.trim());
        ZonedDateTime endDate = ZonedDateTime.parse(end.trim());
        return dto.getByDateRange(startDate, endDate);
    }
}