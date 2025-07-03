package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao itemDao;

    @Transactional
    public Integer createOrder(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
        return orderPojo.getId();
    }

    public OrderPojo getCheckByOrderId(Integer id) {
        OrderPojo order = orderDao.getById(id);
        if (Objects.isNull(order)) {
            throw new ApiException("Order with ID " + id + " not found");
        }
        return order;
    }
    public List<OrderPojo> getAll() {
        return orderDao.getAll();
    }
    public List<OrderPojo> getOrdersByDate(LocalDate date) {
        return orderDao.getByDate(date);
    }


    public List<OrderItemPojo> getOrderItems(Integer orderId) {
        return itemDao.getByOrderId(orderId);
    }

    public void update(Integer id, OrderPojo newPojo) {
        OrderPojo existing = getCheckByOrderId(id);
        existing.setInvoicePath(newPojo.getInvoicePath());
        existing.setTotal(newPojo.getTotal());
        orderDao.update(existing);
    }

}
