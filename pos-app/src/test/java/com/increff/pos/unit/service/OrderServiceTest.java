package com.increff.pos.unit.service;

import com.increff.pos.config.TestData;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderItemDao orderItemDao;

    @InjectMocks
    private OrderService orderService;

    private OrderPojo testOrder;
    private List<OrderItemPojo> testOrderItems;

    @Before
    public void setUp() {
        testOrder = TestData.orderPojo();
        testOrder.setId(1);

        testOrderItems = Arrays.asList(
            TestData.orderItemPojo(1, 1, 2, 99.99),
            TestData.orderItemPojo(2, 2, 1, 149.99)
        );
    }

    @Test
    public void testCreateOrderWithItems_Success() {
        // Given
        doAnswer(invocation -> {
            OrderPojo order = invocation.getArgument(0);
            order.setId(1);
            return null;
        }).when(orderDao).insert(any(OrderPojo.class));

        doNothing().when(orderItemDao).insert(any(OrderItemPojo.class));

        // When
        Integer orderId = orderService.createOrderWithItems(testOrderItems);

        // Then
        assertEquals(Integer.valueOf(1), orderId);
        verify(orderDao, times(1)).insert(any(OrderPojo.class));
        verify(orderItemDao, times(2)).insert(any(OrderItemPojo.class));
    }

    @Test
    public void testGetCheckByOrderId_Success() {
        // Given
        when(orderDao.getById(1)).thenReturn(testOrder);

        // When
        OrderPojo result = orderService.getCheckByOrderId(1);

        // Then
        assertEquals(testOrder, result);
        verify(orderDao, times(1)).getById(1);
    }

    @Test(expected = ApiException.class)
    public void testGetCheckByOrderId_NotFound() {
        // Given
        when(orderDao.getById(1)).thenReturn(null);

        // When
        orderService.getCheckByOrderId(1);

        // Then - exception should be thrown
    }

    @Test
    public void testGetOrderItemsByOrderId_Success() {
        // Given
        when(orderItemDao.getByOrderId(1)).thenReturn(testOrderItems);

        // When
        List<OrderItemPojo> result = orderService.getOrderItemsByOrderId(1);

        // Then
        assertEquals(testOrderItems, result);
        verify(orderItemDao, times(1)).getByOrderId(1);
    }

    @Test
    public void testGetAllOrdersPaginated_Success() {
        // Given
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderDao.getAllOrdersByDate(0, 10)).thenReturn(orders);

        // When
        List<OrderPojo> result = orderService.getAllOrdersPaginated(0, 10);

        // Then
        assertEquals(orders, result);
        verify(orderDao, times(1)).getAllOrdersByDate(0, 10);
    }

    @Test
    public void testCountAll_Success() {
        // Given
        when(orderDao.countAll()).thenReturn(5L);

        // When
        long count = orderService.countAll();

        // Then
        assertEquals(5L, count);
        verify(orderDao, times(1)).countAll();
    }

    @Test
    public void testSearchOrderByQuery_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderDao.searchOrders(start, end, "test", 0, 10)).thenReturn(orders);

        // When
        List<OrderPojo> result = orderService.searchOrderByQuery(start, end, "test", 0, 10);

        // Then
        assertEquals(orders, result);
        verify(orderDao, times(1)).searchOrders(start, end, "test", 0, 10);
    }

    @Test
    public void testCountMatchingOrdersByQuery_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        when(orderDao.countMatchingOrders(start, end, "test")).thenReturn(3L);

        // When
        long count = orderService.countMatchingOrdersByQuery(start, end, "test");

        // Then
        assertEquals(3L, count);
        verify(orderDao, times(1)).countMatchingOrders(start, end, "test");
    }

    @Test
    public void testGetOrdersForSpecificDate_Success() {
        // Given
        ZonedDateTime date = ZonedDateTime.now();
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderDao.getOrdersForSpecificDate(date)).thenReturn(orders);

        // When
        List<OrderPojo> result = orderService.getOrdersForSpecificDate(date);

        // Then
        assertEquals(orders, result);
        verify(orderDao, times(1)).getOrdersForSpecificDate(date);
    }

    @Test
    public void testSaveOrderItems_Success() {
        // Given
        doNothing().when(orderItemDao).insert(any(OrderItemPojo.class));

        // When
        orderService.saveOrderItems(testOrderItems, 1);

        // Then
        verify(orderItemDao, times(2)).insert(any(OrderItemPojo.class));
        // Verify that orderId is set on each item
        for (OrderItemPojo item : testOrderItems) {
            assertEquals(Integer.valueOf(1), item.getOrderId());
        }
    }
} 