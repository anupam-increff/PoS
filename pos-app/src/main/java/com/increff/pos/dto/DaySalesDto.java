package com.increff.pos.dto;

import com.increff.pos.flow.DaySalesFlow;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class DaySalesDto {
    @Autowired
    private DaySalesFlow daySalesFlow;

    public List<DaySalesPojo> getByDateRange(ZonedDateTime start, ZonedDateTime end) {
        return daySalesFlow.getBetween(start, end);
    }
}
