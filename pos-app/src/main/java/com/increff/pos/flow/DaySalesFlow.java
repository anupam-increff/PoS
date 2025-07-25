package com.increff.pos.flow;

import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.DaySalesService;

import com.increff.pos.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.increff.pos.exception.ApiException;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = ApiException.class)
public class DaySalesFlow {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DaySalesService daySalesService;

    public void calculateDailySales(ZonedDateTime date) {
        if (daySalesService.getByDate(date) != null) return;

        List<OrderPojo> orders = orderService.getOrdersForSpecificDate(date);
        SalesMetrics metrics = computeSalesMetrics(orders);
        
        DaySalesPojo dailySales = createDailySalesRecord(date, orders.size(), metrics);
        daySalesService.insert(dailySales);
    }

    public List<DaySalesPojo> getBetween(ZonedDateTime start, ZonedDateTime end) {
        return daySalesService.getBetween(start, end);
    }

    private DaySalesPojo createDailySalesRecord(ZonedDateTime date, int orderCount, SalesMetrics metrics) {
        DaySalesPojo sales = new DaySalesPojo();
        sales.setReportDate(date);
        sales.setInvoicedOrdersCount(orderCount);
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
        int itemCount;
        double revenue;
    }
}
