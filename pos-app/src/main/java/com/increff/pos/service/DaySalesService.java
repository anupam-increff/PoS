package com.increff.pos.service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = ApiException.class)
public class DaySalesService {

    @Autowired
    private DaySalesDao daySalesDao;

    @Autowired
    private OrderService orderService;

    public void insert(DaySalesPojo daySalesPojo) {
        daySalesDao.insert(daySalesPojo);
    }

    public DaySalesPojo getByDate(ZonedDateTime date) {
        return daySalesDao.getReportForDate(date);
    }

    public List<DaySalesPojo> getBetween(ZonedDateTime start, ZonedDateTime end) {
        return daySalesDao.getReportBetweenDates(start, end);
    }

    public DaySalesPojo createDailySalesRecord(ZonedDateTime date, List<OrderPojo> orders) {
        SalesMetrics metrics = computeSalesMetrics(orders);
        DaySalesPojo sales = new DaySalesPojo();
        sales.setReportDate(date);
        sales.setInvoicedOrdersCount(orders.size());
        sales.setInvoicedItemsCount(metrics.itemCount);
        sales.setTotalRevenue(metrics.revenue);
        return sales;
    }

    private SalesMetrics computeSalesMetrics(List<OrderPojo> orders) {
        int totalItems = 0;
        double totalRevenue = 0.0;

        for (OrderPojo order : orders) {
            List<OrderItemPojo> items = orderService.getOrderItemsByOrderId(order.getId());
            for (OrderItemPojo item : items) {
                totalItems += item.getQuantity();
                totalRevenue += item.getSellingPrice() * item.getQuantity();
            }
        }

        return new SalesMetrics(totalItems, totalRevenue);
    }

    @AllArgsConstructor
    private static class SalesMetrics {
        Integer itemCount;
        Double revenue;
    }
}

