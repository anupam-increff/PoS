package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao itemDao;

    @Transactional(rollbackFor = ApiException.class)
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

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return orderDao.getAllPaginated(page, size);
    }
    public List<OrderPojo> getOrdersByDate(ZonedDateTime date) {
        return orderDao.getByDate(date);
    }


    public long countAll() {
        return orderDao.countAll();
    }

    public List<OrderItemPojo> getOrderItems(Integer orderId) {
        return itemDao.getByOrderId(orderId);
    }

    public void update(Integer id, OrderPojo newPojo) {
        OrderPojo existing = getCheckByOrderId(id);
        existing.setInvoiceGenerated(newPojo.getInvoiceGenerated());
        existing.setTotal(newPojo.getTotal());
    }

    public List<OrderPojo> search(ZonedDateTime startDate, ZonedDateTime endDate, Boolean invoiceGenerated, String query, int page, int size) {
        return orderDao.search(startDate, endDate, invoiceGenerated, query, page, size);
    }

    public long countMatching(ZonedDateTime startDate, ZonedDateTime endDate, Boolean invoiceGenerated, String query) {
        return orderDao.countMatching(startDate, endDate, invoiceGenerated, query);
    }
}
