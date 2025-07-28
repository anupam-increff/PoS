package com.increff.pos.order.unit.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderItemDao orderItemDao;

    @Test
    public void testCreateOrderWithItems() {
        OrderItemPojo item = new OrderItemPojo();
        item.setProductId(1);
        item.setQuantity(5);
        item.setSellingPrice(100.0);

        doAnswer(invocation -> {
            OrderPojo orderPojo = invocation.getArgument(0);
            orderPojo.setId(1);
            return null;
        }).when(orderDao).insert(any(OrderPojo.class));

        Integer orderId = service.createOrderWithItems(Arrays.asList(item));
        assertNotNull(orderId);
        assertEquals(Integer.valueOf(1), orderId);
        verify(orderDao).insert(any(OrderPojo.class));
        verify(orderItemDao).insert(any(OrderItemPojo.class));
    }

    @Test
    public void testGetOrderItemsByOrderId() {
        OrderItemPojo item1 = new OrderItemPojo();
        item1.setOrderId(1);
        item1.setProductId(1);
        item1.setQuantity(5);
        item1.setSellingPrice(100.0);

        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(1);
        item2.setProductId(2);
        item2.setQuantity(3);
        item2.setSellingPrice(200.0);

        when(orderItemDao.getByOrderId(1)).thenReturn(Arrays.asList(item1, item2));

        List<OrderItemPojo> items = service.getOrderItemsByOrderId(1);
        assertEquals(2, items.size());
    }

    @Test
    public void testGetCheckByOrderId() {
        OrderPojo order = new OrderPojo();
        order.setId(1);
        order.setOrderStatus(OrderStatus.CREATED);
        when(orderDao.getById(1)).thenReturn(order);

        OrderPojo fetched = service.getCheckByOrderId(1);
        assertNotNull(fetched);
        assertEquals(order.getId(), fetched.getId());
        assertEquals(order.getOrderStatus(), fetched.getOrderStatus());
    }

    @Test
    public void testGetAllOrdersPaginated() {
        OrderPojo order1 = new OrderPojo();
        order1.setId(1);
        order1.setOrderStatus(OrderStatus.CREATED);

        OrderPojo order2 = new OrderPojo();
        order2.setId(2);
        order2.setOrderStatus(OrderStatus.CREATED);

        when(orderDao.getAllOrdersByDate(0, 10)).thenReturn(Arrays.asList(order1, order2));

        List<OrderPojo> orders = service.getAllOrdersPaginated(0, 10);
        assertEquals(2, orders.size());
    }

    @Test
    public void testCountAll() {
        when(orderDao.countAll()).thenReturn(5L);

        Long count = service.countAll();
        assertEquals(Long.valueOf(5), count);
    }

    @Test
    public void testSearchOrderByQuery() {
        ZonedDateTime startDate = ZonedDateTime.now().minusDays(1);
        ZonedDateTime endDate = ZonedDateTime.now();
        String query = "123";

        OrderPojo order1 = new OrderPojo();
        order1.setId(1);
        order1.setOrderStatus(OrderStatus.CREATED);

        when(orderDao.searchOrders(eq(startDate), eq(endDate), eq(query), eq(0), eq(10)))
                .thenReturn(Arrays.asList(order1));

        List<OrderPojo> orders = service.searchOrderByQuery(startDate, endDate, query, 0, 10);
        assertEquals(1, orders.size());
    }

    @Test
    public void testCountMatchingOrdersByQuery() {
        ZonedDateTime startDate = ZonedDateTime.now().minusDays(1);
        ZonedDateTime endDate = ZonedDateTime.now();
        String query = "123";

        when(orderDao.countMatchingOrders(eq(startDate), eq(endDate), eq(query)))
                .thenReturn(5L);

        Long count = service.countMatchingOrdersByQuery(startDate, endDate, query);
        assertEquals(Long.valueOf(5), count);
    }

    @Test
    public void testUpdateOrderStatus() {
        OrderPojo order = new OrderPojo();
        order.setId(1);
        order.setOrderStatus(OrderStatus.CREATED);
        when(orderDao.getById(1)).thenReturn(order);

        service.updateOrderStatus(1, OrderStatus.INVOICE_GENERATED);
        assertEquals(OrderStatus.INVOICE_GENERATED, order.getOrderStatus());
    }
} 