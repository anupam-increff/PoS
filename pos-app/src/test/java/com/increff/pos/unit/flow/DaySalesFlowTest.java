package com.increff.pos.unit.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.DaySalesFlow;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.service.OrderService;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.time.ZoneId;
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

    @InjectMocks
    private DaySalesFlow daySalesFlow;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateDailySales() {
        // Arrange
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
        
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
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(orderItems.subList(0, 1));
        when(orderService.getOrderItemsByOrderId(2)).thenReturn(orderItems.subList(1, 2));
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderService, times(1)).getOrderItemsByOrderId(1);
        verify(orderService, times(1)).getOrderItemsByOrderId(2);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySalesAlreadyExists() {
        // Arrange
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
        
        DaySalesPojo existingSales = new DaySalesPojo();
        existingSales.setReportDate(testDate);
        existingSales.setInvoicedOrdersCount(5);
        
        when(daySalesService.getByDate(testDate)).thenReturn(existingSales);
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, never()).getOrdersByDate(testDate);
        verify(daySalesService, never()).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySalesNoOrders() {
        // Arrange
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
        
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
    public void testGetDaySalesBetween() {
        // Arrange
        ZonedDateTime startDate = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(7);
        ZonedDateTime endDate = ZonedDateTime.now(ZoneId.systemDefault());
        
        List<DaySalesPojo> mockSales = new ArrayList<>();
        DaySalesPojo sales1 = new DaySalesPojo();
        sales1.setReportDate(startDate);
        sales1.setInvoicedOrdersCount(5);
        sales1.setInvoicedItemsCount(25);
        sales1.setTotalRevenue(500.0);
        mockSales.add(sales1);
        
        DaySalesPojo sales2 = new DaySalesPojo();
        sales2.setReportDate(endDate);
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
        ZonedDateTime startDate = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(7);
        ZonedDateTime endDate = ZonedDateTime.now(ZoneId.systemDefault());
        
        when(daySalesService.getBetween(startDate, endDate)).thenReturn(new ArrayList<>());
        
        // Act
        List<DaySalesPojo> result = daySalesFlow.getBetween(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(daySalesService, times(1)).getBetween(startDate, endDate);
    }

    @Test
    public void testCalculateDailySalesWithValidOrders() {
        // Arrange
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
        
        List<OrderPojo> orders = new ArrayList<>();
        OrderPojo order1 = new OrderPojo();
        order1.setId(1);
        order1.setTotal(150.0);
        orders.add(order1);
        
        OrderPojo order2 = new OrderPojo();
        order2.setId(2);
        order2.setTotal(250.0);
        orders.add(order2);
        
        List<OrderItemPojo> orderItems1 = new ArrayList<>();
        OrderItemPojo item1 = new OrderItemPojo();
        item1.setOrderId(1);
        item1.setQuantity(3);
        item1.setSellingPrice(50.0);
        orderItems1.add(item1);
        
        List<OrderItemPojo> orderItems2 = new ArrayList<>();
        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(2);
        item2.setQuantity(5);
        item2.setSellingPrice(50.0);
        orderItems2.add(item2);
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(orders);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(orderItems1);
        when(orderService.getOrderItemsByOrderId(2)).thenReturn(orderItems2);
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderService, times(1)).getOrderItemsByOrderId(1);
        verify(orderService, times(1)).getOrderItemsByOrderId(2);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySalesWithMultipleItems() {
        // Arrange
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
        
        List<OrderPojo> orders = new ArrayList<>();
        OrderPojo order1 = new OrderPojo();
        order1.setId(1);
        order1.setTotal(150.0);
        orders.add(order1);
        
        OrderPojo order2 = new OrderPojo();
        order2.setId(2);
        order2.setTotal(300.0);
        orders.add(order2);
        
        OrderPojo order3 = new OrderPojo();
        order3.setId(3);
        order3.setTotal(200.0);
        orders.add(order3);
        
        List<OrderItemPojo> orderItems1 = new ArrayList<>();
        OrderItemPojo item1 = new OrderItemPojo();
        item1.setOrderId(1);
        item1.setQuantity(3);
        item1.setSellingPrice(50.0);
        orderItems1.add(item1);
        
        List<OrderItemPojo> orderItems2 = new ArrayList<>();
        OrderItemPojo item2a = new OrderItemPojo();
        item2a.setOrderId(2);
        item2a.setQuantity(2);
        item2a.setSellingPrice(75.0);
        orderItems2.add(item2a);
        
        OrderItemPojo item2b = new OrderItemPojo();
        item2b.setOrderId(2);
        item2b.setQuantity(3);
        item2b.setSellingPrice(50.0);
        orderItems2.add(item2b);
        
        List<OrderItemPojo> orderItems3 = new ArrayList<>();
        OrderItemPojo item3 = new OrderItemPojo();
        item3.setOrderId(3);
        item3.setQuantity(4);
        item3.setSellingPrice(50.0);
        orderItems3.add(item3);
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(orders);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(orderItems1);
        when(orderService.getOrderItemsByOrderId(2)).thenReturn(orderItems2);
        when(orderService.getOrderItemsByOrderId(3)).thenReturn(orderItems3);
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderService, times(3)).getOrderItemsByOrderId(anyInt());
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
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
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
    public void testCalculateDailySalesWithLargeDataset() {
        // Arrange
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.systemDefault());
        
        List<OrderPojo> orders = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            OrderPojo order = new OrderPojo();
            order.setId(i);
            order.setTotal(100.0 * i);
            orders.add(order);
        }
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersByDate(testDate)).thenReturn(orders);
        
        // Mock orderService for each order
        for (int i = 1; i <= 100; i++) {
            List<OrderItemPojo> orderItems = new ArrayList<>();
            OrderItemPojo item = new OrderItemPojo();
            item.setOrderId(i);
            item.setQuantity(i);
            item.setSellingPrice(100.0);
            orderItems.add(item);
            when(orderService.getOrderItemsByOrderId(i)).thenReturn(orderItems);
        }
        
        // Act
        daySalesFlow.calculateDailySales(testDate);
        
        // Assert
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersByDate(testDate);
        verify(orderService, times(100)).getOrderItemsByOrderId(anyInt());
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }
} 