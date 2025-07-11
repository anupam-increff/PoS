package com.increff.pos.flow;

import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.increff.pos.exception.ApiException;
import java.time.LocalDate;
import java.util.List;

@Service
public class DaySalesFlow {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private DaySalesService daySalesService;

    @Transactional(rollbackFor = ApiException.class)
    public void calculateDailySales(LocalDate date) {
        if (daySalesService.getByDate(date) != null) return;

        List<OrderPojo> orders = orderService.getOrdersByDate(date);
        SalesMetrics metrics = computeSalesMetrics(orders);
        
        DaySalesPojo dailySales = createDailySalesRecord(date, orders.size(), metrics);
        daySalesService.insert(dailySales);
    }

    public List<DaySalesPojo> getBetween(LocalDate start, LocalDate end) {
        return daySalesService.getBetween(start, end);
    }

    private DaySalesPojo createDailySalesRecord(LocalDate date, int orderCount, SalesMetrics metrics) {
        DaySalesPojo sales = new DaySalesPojo();
        sales.setDate(date);
        sales.setInvoicedOrdersCount(orderCount);
        sales.setInvoicedItemsCount(metrics.itemCount);
        sales.setTotalRevenue(metrics.revenue);
        return sales;
    }

    private SalesMetrics computeSalesMetrics(List<OrderPojo> orders) {
        int totalItems = 0;
        double totalRevenue = 0.0;

        for (OrderPojo order : orders) {
            List<OrderItemPojo> items = orderItemService.getByOrderId(order.getId());
            for (OrderItemPojo item : items) {
                totalItems += item.getQuantity();
            }
            totalRevenue += order.getTotal();
        }

        return new SalesMetrics(totalItems, totalRevenue);
    }

    @AllArgsConstructor
    private static class SalesMetrics {
        int itemCount;
        double revenue;
    }
}
