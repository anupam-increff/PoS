package com.increff.pos.dto;

import com.increff.pos.flow.DaySalesFlow;
import com.increff.pos.model.data.DaySalesData;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DaySalesDto extends BaseDto {

    @Autowired
    private DaySalesFlow daySalesFlow;

    @Autowired
    private DaySalesService daySalesService;

    public List<DaySalesData> getBetween(ZonedDateTime start, ZonedDateTime end) {
        List<DaySalesPojo> sales = daySalesService.getBetween(start, end);
        return sales.stream().map(this::convertToData).collect(Collectors.toList());
    }

    public List<DaySalesData> getByDateRange(ZonedDateTime start, ZonedDateTime end) {
        return getBetween(start, end);
    }

    private DaySalesData convertToData(DaySalesPojo pojo) {
        return ConvertUtil.convert(pojo, DaySalesData.class);
    }
}
