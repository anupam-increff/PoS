package com.increff.pos.order.integration.dao;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.setup.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class OrderDaoIntegrationTest extends AbstractTest {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Test
    public void testInsert() {
        OrderPojo order = new OrderPojo();
        order.setOrderStatus(OrderStatus.CREATED);
        orderDao.insert(order);

        assertNotNull(order.getId());
    }

    @Test
    public void testGetById() {
        OrderPojo order = new OrderPojo();
        order.setOrderStatus(OrderStatus.CREATED);
        orderDao.insert(order);

        OrderPojo fetched = orderDao.getById(order.getId());
        assertNotNull(fetched);
        assertEquals(order.getId(), fetched.getId());
        assertEquals(order.getOrderStatus(), fetched.getOrderStatus());
    }

    @Test
    public void testGetByIdNotFound() {
        OrderPojo fetched = orderDao.getById(999);
        assertNull(fetched);
    }

    @Test
    public void testGetAll() {
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.CREATED);

        List<OrderPojo> orders = orderDao.getAllOrdersByDate(0, 10);
        assertEquals(2, orders.size());
    }

    @Test
    public void testGetAllPagination() {
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.CREATED);

        List<OrderPojo> page1 = orderDao.getAllOrdersByDate(0, 2);
        assertEquals(2, page1.size());

        List<OrderPojo> page2 = orderDao.getAllOrdersByDate(1, 2);
        assertEquals(1, page2.size());
    }

    @Test
    public void testCountAll() {
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.CREATED);

        Long count = orderDao.countAll();
        assertEquals(Long.valueOf(2), count);
    }

    @Test
    public void testSearchOrders() {
        ZonedDateTime now = ZonedDateTime.now();
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.CREATED);

        List<OrderPojo> orders = orderDao.searchOrders(now.minusDays(1), now.plusDays(1), "", 0, 10);
        assertEquals(2, orders.size());
    }

    @Test
    public void testCountMatchingOrders() {
        ZonedDateTime now = ZonedDateTime.now();
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.CREATED);

        long count = orderDao.countMatchingOrders(now.minusDays(1), now.plusDays(1), "");
        assertEquals(2L, count);
    }

    private OrderPojo createOrder(OrderStatus status) {
        OrderPojo order = new OrderPojo();
        order.setOrderStatus(status);
        orderDao.insert(order);
        return order;
    }
} 