package com.increff.pos.service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DaySalesService {

    @Autowired private DaySalesDao daySalesDao;
    @Autowired private OrderService orderService;
    @Autowired private OrderItemService orderItemService;

    @Transactional
    @Scheduled(cron = "00 59 23 * * *" , zone = "UTC") // Runs daily at EOD UTC
    public void calculateDailySales() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        if (daySalesDao.getByDate(today) != null) return; // prevent double-insert

        List<OrderPojo> orders = orderService.getOrdersByDate(today); // implement this method
        int orderCount = orders.size();
        int itemCount = 0;
        double totalRevenue = 0;

        for (OrderPojo order : orders) {
            List<OrderItemPojo> items = orderItemService.getByOrderId(order.getId());
            for (OrderItemPojo i : items) {
                itemCount += i.getQuantity();
                totalRevenue += i.getQuantity() * i.getSellingPrice();
            }
        }

        DaySalesPojo pojo = new DaySalesPojo();
        pojo.setDate(today);
        pojo.setInvoicedOrdersCount(orderCount);
        pojo.setInvoicedItemsCount(itemCount);
        pojo.setTotalRevenue(totalRevenue);
        daySalesDao.insert(pojo);
    }

    public List<DaySalesPojo> getBetween(LocalDate start, LocalDate end) {
        return daySalesDao.getBetween(start, end);
    }
}
