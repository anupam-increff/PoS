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
        if (orderItemPojos == null || orderItemPojos.isEmpty()) {
            throw new ApiException("Order must have at least one item");
        }
        OrderPojo order = new OrderPojo();
        order.setOrderStatus(OrderStatus.CREATED);
        Integer orderId = placeOrder(order);
        saveOrderItems(orderItemPojos, orderId);
        return orderId;
    }

    private Integer placeOrder(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
        return orderPojo.getId();
    }

    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        OrderPojo order = getCheckByOrderId(orderId);
        validateStatusTransitionRule(order.getOrderStatus(), newStatus);
        order.setOrderStatus(newStatus);
    }

    private void validateStatusTransitionRule(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }
        switch (currentStatus) {
            case CREATED:
                if (newStatus != OrderStatus.INVOICE_GENERATED) {
                    throw new ApiException("Invalid status transition. Order in CREATED state can only move to INVOICE_GENERATED state");
                }
                break;
            case INVOICE_GENERATED:
                throw new ApiException("Cannot change status of an order once invoice is generated");
        }
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

    public List<OrderPojo> getAllOrdersPaginated(Integer page, Integer pageSize) {
        return orderDao.getAllOrdersByDate(page, pageSize);
    }

    public Long countAll() {
        return orderDao.countAll();
    }

    public List<OrderPojo> searchOrderByQuery(ZonedDateTime startDate, ZonedDateTime endDate, String query, Integer page, Integer pageSize) {
        return orderDao.searchOrders(startDate, endDate, query, page, pageSize);
    }

    public Long countMatchingOrdersByQuery(ZonedDateTime startDate, ZonedDateTime endDate, String query) {
        return orderDao.countMatchingOrders(startDate, endDate, query);
    }

    public List<OrderPojo> getOrdersForSpecificDate(ZonedDateTime date) {
        return orderDao.getOrdersForSpecificDate(date);
    }
}