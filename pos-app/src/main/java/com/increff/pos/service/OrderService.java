package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.OrderStatus;
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

    public Integer createOrderWithItems(List<OrderItemPojo> orderItemPojos) {
        OrderPojo order = new OrderPojo();
        Integer orderId = placeOrder(order);
        saveOrderItems(orderItemPojos, orderId);
        return orderId;
    }

    private Integer placeOrder(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
        return orderPojo.getId();
    }

    public void updateOrderStatus(Integer orderId, OrderStatus status) {
        OrderPojo order = getCheckByOrderId(orderId);
        order.setOrderStatus(status);
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

    public void saveOrderItems(List<OrderItemPojo> orderItemPojos, Integer orderId) {
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            orderItemPojo.setOrderId(orderId);
            orderItemDao.insert(orderItemPojo);
        }
    }

    public List<OrderPojo> getAllOrdersPaginated(int page, int size) {
        return orderDao.getAllOrdersByDate(page, size);
    }

    public long countAll() {
        return orderDao.countAll();
    }

    public List<OrderPojo> searchOrderByQuery(ZonedDateTime startDate, ZonedDateTime endDate, String query, int page, int size) {
        return orderDao.searchOrders(startDate, endDate, query, page, size);
    }

    public long countMatchingOrdersByQuery(ZonedDateTime startDate, ZonedDateTime endDate, String query) {
        return orderDao.countMatchingOrders(startDate, endDate, query);
    }

    public List<OrderPojo> getOrdersForSpecificDate(ZonedDateTime date) {
        return orderDao.getOrdersForSpecificDate(date);
    }
}
