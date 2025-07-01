package com.increff.pos.dto;

import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DaySalesDto {
    @Autowired private DaySalesService service;

    public List<DaySalesPojo> getByDateRange(LocalDate start, LocalDate end) {
        return service.getBetween(start, end);
    }
    public void generateTodayReport() {
        service.calculateDailySales();
    }

}
