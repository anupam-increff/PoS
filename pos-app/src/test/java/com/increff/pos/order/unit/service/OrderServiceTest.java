package com.increff.pos.order.unit.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderService;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
        
        OrderItemPojo item1 = TestData.orderItemPojo(1, 1, 2, 50.0);
        OrderItemPojo item2 = TestData.orderItemPojo(2, 2, 1, 25.0);
        testOrderItems = Arrays.asList(item1, item2);
    }

    /**
     * Tests creating an order with items successfully.
     * Verifies proper order ID generation and database insertion.
     */
    @Test
    public void testCreateOrderWithItems() {
        // Given
        doAnswer(invocation -> {
            OrderPojo order = invocation.getArgument(0);
            order.setId(123); // Simulate ID generation
            return null;
        }).when(orderDao).insert(any(OrderPojo.class));
        doNothing().when(orderItemDao).insert(any(OrderItemPojo.class));

        // When
        Integer orderId = orderService.createOrderWithItems(testOrderItems);

        // Then
        assertNotNull("Order ID should be generated", orderId);
        assertEquals(Integer.valueOf(123), orderId);
        verify(orderDao, times(1)).insert(any(OrderPojo.class));
        verify(orderItemDao, times(2)).insert(any(OrderItemPojo.class));
    }

    /**
     * Tests creating order with null items list.
     * Verifies proper error handling for null input.
     */
    @Test(expected = NullPointerException.class)
    public void testCreateOrderWithNullItems() {
        // When - Try to create order with null items
        orderService.createOrderWithItems(null);
        
        // Then - Exception should be thrown
    }

    /**
     * Tests creating order with empty items list.
     * Verifies service processes empty collections without error.
     */
    @Test
    public void testCreateOrderWithEmptyItems() {
        // Given - Empty items list
        List<OrderItemPojo> emptyItems = new ArrayList<>();
        doAnswer(invocation -> {
            OrderPojo order = invocation.getArgument(0);
            order.setId(123); // Simulate ID generation
            return null;
        }).when(orderDao).insert(any(OrderPojo.class));
        
        // When - Try to create order with empty items
        Integer orderId = orderService.createOrderWithItems(emptyItems);
        
        // Then - Order should be created without items
        assertNotNull("Order should be created even with empty items", orderId);
        assertEquals(Integer.valueOf(123), orderId);
        verify(orderDao, times(1)).insert(any(OrderPojo.class));
        verify(orderItemDao, times(0)).insert(any(OrderItemPojo.class)); // No items to insert
    }

    /**
     * Tests retrieving order by valid ID.
     * Verifies successful order lookup and validation.
     */
    @Test
    public void testGetCheckByOrderId() {
        // Given
        when(orderDao.getById(1)).thenReturn(testOrder);

        // When
        OrderPojo result = orderService.getCheckByOrderId(1);

        // Then
        assertEquals("Order should match", testOrder, result);
        verify(orderDao, times(1)).getById(1);
    }

    /**
     * Tests retrieving order by invalid ID.
     * Verifies proper exception handling for non-existent orders.
     */
    @Test(expected = ApiException.class)
    public void testGetCheckByOrderIdNotFound() {
        // Given
        when(orderDao.getById(999)).thenReturn(null);

        // When
        orderService.getCheckByOrderId(999);
        
        // Then - Exception should be thrown
    }

    /**
     * Tests retrieving order items by order ID.
     * Verifies proper delegation to DAO layer.
     */
    @Test
    public void testGetOrderItemsByOrderId() {
        // Given
        when(orderItemDao.getByOrderId(1)).thenReturn(testOrderItems);

        // When
        List<OrderItemPojo> result = orderService.getOrderItemsByOrderId(1);

        // Then
        assertEquals("Order items should match", testOrderItems, result);
        verify(orderItemDao, times(1)).getByOrderId(1);
    }

    /**
     * Tests retrieving all orders with pagination.
     * Verifies proper delegation to DAO layer with pagination.
     */
    @Test
    public void testGetAllOrdersPaginated() {
        // Given
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderDao.getAllOrdersByDate(0, 10)).thenReturn(orders);

        // When
        List<OrderPojo> result = orderService.getAllOrdersPaginated(0, 10);

        // Then
        assertEquals("Results should match DAO response", orders, result);
        verify(orderDao, times(1)).getAllOrdersByDate(0, 10);
    }

    /**
     * Tests counting all orders in the system.
     * Verifies proper count delegation to DAO layer.
     */
    @Test
    public void testCountAll() {
        // Given
        when(orderDao.countAll()).thenReturn(5L);

        // When
        long result = orderService.countAll();

        // Then
        assertEquals("Count should match DAO response", 5L, result);
        verify(orderDao, times(1)).countAll();
    }
} 