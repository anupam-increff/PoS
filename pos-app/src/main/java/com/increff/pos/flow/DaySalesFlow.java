package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = ApiException.class)
public class DaySalesFlow {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DaySalesService daySalesService;

    public void calculateDailySales(ZonedDateTime date) {
        if (Objects.nonNull(daySalesService.getByDate(date))) return;

        List<OrderPojo> orders = orderService.getOrdersForSpecificDate(date);
        DaySalesPojo dailySales = daySalesService.createDailySalesRecord(date, orders);
        daySalesService.insert(dailySales);
    }

    public List<DaySalesPojo> getBetween(ZonedDateTime start, ZonedDateTime end) {
        return daySalesService.getBetween(start, end);
    }
}
