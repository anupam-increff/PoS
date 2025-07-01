package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao itemDao;

    @Transactional
    public Integer createOrder(List<OrderItemPojo> items) {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        orderDao.insert(order);

        for (OrderItemPojo item : items) {
            item.setOrderId(order.getId());
            itemDao.insert(item);
        }
        return order.getId();
    }

    public OrderPojo get(Integer id) {
        OrderPojo order = orderDao.select(id);
        if (order == null) {
            throw new ApiException("Order with ID " + id + " not found");
        }
        return order;
    }
    public List<OrderPojo> getAll() {
        return orderDao.selectAll();
    }
    public List<OrderPojo> getOrdersByDate(LocalDate date) {
        return orderDao.selectByDate(date);
    }


    public List<OrderItemPojo> getOrderItems(Integer orderId) {
        return itemDao.selectByOrderId(orderId);
    }

    public void update(Integer id, OrderPojo newPojo) {
        OrderPojo existing = get(id);
        existing.setInvoicePath(newPojo.getInvoicePath());
        existing.setTotal(newPojo.getTotal());
        orderDao.update(existing);
    }

}
