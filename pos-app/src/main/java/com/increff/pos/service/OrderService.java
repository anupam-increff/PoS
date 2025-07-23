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
@Transactional(rollbackFor = ApiException.class)
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional(rollbackFor = ApiException.class)
    public Integer createOrder(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
        return orderPojo.getId();
    }

    public void saveOrderItems(List<OrderItemPojo> orderItemPojos, Integer orderId) {
        for (OrderItemPojo item : orderItemPojos) {
            item.setOrderId(orderId);
            orderItemDao.insert(item);
        }
    }

    public void addOrderItem(OrderItemPojo orderItemPojo) {
        orderItemDao.insert(orderItemPojo);
    }

    public List<OrderItemPojo> getOrderItemsByOrderId(Integer orderId) {
        return orderItemDao.getByOrderId(orderId);
    }

    public OrderPojo getCheckByOrderId(Integer id) {
        OrderPojo order = orderDao.getById(id);
        if (Objects.isNull(order)) {
            throw new ApiException("Order with ID " + id + " not found");
        }
        return order;
    }

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return orderDao.getAllOrdersByDate(page, size);
    }
    
    public List<OrderPojo> getOrdersByDate(ZonedDateTime date) {
        return orderDao.getOrdersForSpecificDate(date);
    }

    public long countAll() {
        return orderDao.countAll();
    }

    public void update(Integer id, OrderPojo newPojo) {
        OrderPojo existing = getCheckByOrderId(id);
        existing.setTotal(newPojo.getTotal());
    }

    public List<OrderPojo> search(ZonedDateTime startDate, ZonedDateTime endDate, String query, int page, int size) {
        return orderDao.searchOrders(startDate, endDate, query, page, size);
    }

    public long countMatching(ZonedDateTime startDate, ZonedDateTime endDate, String query) {
        return orderDao.countMatchingOrders(startDate, endDate, query);
    }
}
