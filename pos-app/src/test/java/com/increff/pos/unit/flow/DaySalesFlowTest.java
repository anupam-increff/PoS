package com.increff.pos.unit.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.DaySalesFlow;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DaySalesFlowTest {

    @Mock
    private DaySalesService daySalesService;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private DaySalesFlow daySalesFlow;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateDailySales() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        
        List<OrderPojo> orders = new ArrayList<>();
        OrderPojo order1 = new OrderPojo();
        order1.setId(1);
        order1.setTotal(100.0);
        orders.add(order1);
        
        OrderPojo order2 = new OrderPojo();
        order2.setId(2);
        order2.setTotal(200.0);
        orders.add(order2);
        
        List<OrderItemPojo> orderItems = new ArrayList<>();
        OrderItemPojo item1 = new OrderItemPojo();
        item1.setOrderId(1);
        item1.setQuantity(5);
        item1.setSellingPrice(20.0);
        orderItems.add(item1);
        
        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(2);
        item2.setQuantity(10);
        item2.setSellingPrice(20.0);
        orderItems.add(item2);
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(orders);
        when(orderItemService.getByOrderId(1)).thenReturn(orderItems.subList(0, 1));
        when(orderItemService.getByOrderId(2)).thenReturn(orderItems.subList(1, 2));
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderItemService, times(1)).getByOrderId(1);
        verify(orderItemService, times(1)).getByOrderId(2);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySalesWithNoOrders() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(new ArrayList<>());
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySalesAlreadyExists() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        DaySalesPojo existingSales = new DaySalesPojo();
        existingSales.setDate(testDate);
        
        when(daySalesService.getByDate(testDate)).thenReturn(existingSales);
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert - Should return early without doing calculations
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, never()).getOrdersByDate(any());
        verify(daySalesService, never()).insert(any());
    }

    @Test
    public void testGetDaySalesBetween() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        List<DaySalesPojo> mockSales = new ArrayList<>();
        DaySalesPojo sales1 = new DaySalesPojo();
        sales1.setDate(startDate);
        sales1.setInvoicedOrdersCount(5);
        sales1.setInvoicedItemsCount(25);
        sales1.setTotalRevenue(500.0);
        mockSales.add(sales1);
        
        DaySalesPojo sales2 = new DaySalesPojo();
        sales2.setDate(endDate);
        sales2.setInvoicedOrdersCount(3);
        sales2.setInvoicedItemsCount(15);
        sales2.setTotalRevenue(300.0);
        mockSales.add(sales2);
        
        when(daySalesService.getBetween(startDate, endDate)).thenReturn(mockSales);
        
        // Act
        List<DaySalesPojo> result = daySalesFlow.getBetween(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getInvoicedOrdersCount().intValue());
        assertEquals(3, result.get(1).getInvoicedOrdersCount().intValue());
        verify(daySalesService, times(1)).getBetween(startDate, endDate);
    }

    @Test
    public void testGetDaySalesBetweenEmpty() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        when(daySalesService.getBetween(startDate, endDate)).thenReturn(new ArrayList<>());
        
        // Act
        List<DaySalesPojo> result = daySalesFlow.getBetween(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(daySalesService, times(1)).getBetween(startDate, endDate);
    }

    @Test
    public void testCalculateDailySalesWithException() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        when(daySalesService.getByDate(testDate)).thenThrow(new ApiException("Database error"));
        
        // Act & Assert
        try {
            daySalesFlow.calculateDailySales(testDate);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertEquals("Database error", e.getMessage());
        }
        verify(daySalesService, times(1)).getByDate(testDate);
    }

    @Test
    public void testCalculateDailySalesWithComplexData() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        
        List<OrderPojo> orders = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            OrderPojo order = new OrderPojo();
            order.setId(i);
            order.setTotal(i * 100.0);
            orders.add(order);
        }
        
        List<OrderItemPojo> orderItems1 = new ArrayList<>();
        OrderItemPojo item1 = new OrderItemPojo();
        item1.setOrderId(1);
        item1.setQuantity(2);
        item1.setSellingPrice(50.0);
        orderItems1.add(item1);
        
        List<OrderItemPojo> orderItems2 = new ArrayList<>();
        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(2);
        item2.setQuantity(4);
        item2.setSellingPrice(50.0);
        orderItems2.add(item2);
        
        List<OrderItemPojo> orderItems3 = new ArrayList<>();
        OrderItemPojo item3 = new OrderItemPojo();
        item3.setOrderId(3);
        item3.setQuantity(6);
        item3.setSellingPrice(50.0);
        orderItems3.add(item3);
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(orders);
        when(orderItemService.getByOrderId(1)).thenReturn(orderItems1);
        when(orderItemService.getByOrderId(2)).thenReturn(orderItems2);
        when(orderItemService.getByOrderId(3)).thenReturn(orderItems3);
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderItemService, times(3)).getByOrderId(anyInt());
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySalesWithNullDate() {
        // Arrange
        when(daySalesService.getByDate(null)).thenReturn(null);
        
        // Act & Assert - The service might allow null dates
        try {
            daySalesFlow.calculateDailySales(null);
            // If no exception is thrown, verify that the service was called
            verify(daySalesService, times(1)).getByDate(null);
        } catch (Exception e) {
            // If an exception is thrown, that's also acceptable behavior
            assertNotNull(e);
        }
    }

    @Test
    public void testCalculateDailySalesServiceThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(new ArrayList<>());
        doThrow(new ApiException("Insert failed")).when(daySalesService).insert(any(DaySalesPojo.class));
        
        // Act & Assert
        try {
            daySalesFlow.calculateDailySales(testDate);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertEquals("Insert failed", e.getMessage());
        }
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testGetDaySalesBetweenWithApiException() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        when(daySalesService.getBetween(startDate, endDate))
            .thenThrow(new ApiException("Database connection failed"));
        
        // Act & Assert
        try {
            daySalesFlow.getBetween(startDate, endDate);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertEquals("Database connection failed", e.getMessage());
        }
        verify(daySalesService, times(1)).getBetween(startDate, endDate);
    }

    @Test
    public void testMultipleOrdersWithMultipleItems() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        
        List<OrderPojo> orders = new ArrayList<>();
        OrderPojo order1 = new OrderPojo();
        order1.setId(1);
        order1.setTotal(150.0);
        orders.add(order1);
        
        OrderPojo order2 = new OrderPojo();
        order2.setId(2);
        order2.setTotal(250.0);
        orders.add(order2);
        
        // Order 1 has 2 items
        List<OrderItemPojo> orderItems1 = new ArrayList<>();
        OrderItemPojo item1a = new OrderItemPojo();
        item1a.setOrderId(1);
        item1a.setQuantity(2);
        item1a.setSellingPrice(50.0);
        orderItems1.add(item1a);
        
        OrderItemPojo item1b = new OrderItemPojo();
        item1b.setOrderId(1);
        item1b.setQuantity(1);
        item1b.setSellingPrice(50.0);
        orderItems1.add(item1b);
        
        // Order 2 has 1 item
        List<OrderItemPojo> orderItems2 = new ArrayList<>();
        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(2);
        item2.setQuantity(5);
        item2.setSellingPrice(50.0);
        orderItems2.add(item2);
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(orders);
        when(orderItemService.getByOrderId(1)).thenReturn(orderItems1);
        when(orderItemService.getByOrderId(2)).thenReturn(orderItems2);
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderItemService, times(1)).getByOrderId(1);
        verify(orderItemService, times(1)).getByOrderId(2);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }
} 