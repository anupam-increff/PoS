package com.increff.pos.unit.flow;

import com.increff.pos.config.TestData;
import com.increff.pos.flow.DaySalesFlow;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.DaySalesService;
import com.increff.pos.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DaySalesFlowTest {

    @Mock
    private OrderService orderService;

    @Mock
    private DaySalesService daySalesService;

    @InjectMocks
    private DaySalesFlow daySalesFlow;

    private ZonedDateTime testDate;
    private OrderPojo order1, order2;
    private List<OrderItemPojo> order1Items, order2Items;

    @Before
    public void setUp() {
        testDate = ZonedDateTime.now();
        
        order1 = TestData.orderPojo();
        order1.setId(1);
        
        order2 = TestData.orderPojo();
        order2.setId(2);
        
        order1Items = Arrays.asList(
            TestData.orderItemPojo(1, 1, 2, 50.0),
            TestData.orderItemPojo(2, 2, 3, 30.0)
        );
        
        order2Items = Arrays.asList(
            TestData.orderItemPojo(3, 3, 1, 100.0)
        );
    }

    @Test
    public void testCalculateDailySales_NoExistingData() {
        // Given
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(Arrays.asList(order1, order2));
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(order1Items);
        when(orderService.getOrderItemsByOrderId(2)).thenReturn(order2Items);

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersForSpecificDate(testDate);
        verify(orderService, times(1)).getOrderItemsByOrderId(1);
        verify(orderService, times(1)).getOrderItemsByOrderId(2);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySales_ExistingDataSkipped() {
        // Given
        DaySalesPojo existingSales = TestData.daySales(testDate);
        when(daySalesService.getByDate(testDate)).thenReturn(existingSales);

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, never()).getOrdersForSpecificDate(any());
        verify(daySalesService, never()).insert(any());
    }

    @Test
    public void testCalculateDailySales_NoOrders() {
        // Given
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(new ArrayList<>());

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).getByDate(testDate);
        verify(orderService, times(1)).getOrdersForSpecificDate(testDate);
        verify(daySalesService, times(1)).insert(any(DaySalesPojo.class));
    }

    @Test
    public void testCalculateDailySales_MetricsCalculation() {
        // Given
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(Arrays.asList(order1, order2));
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(order1Items);
        when(orderService.getOrderItemsByOrderId(2)).thenReturn(order2Items);

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).insert(argThat(sales -> {
            // Expected: order1 (2*50 + 3*30 = 190) + order2 (1*100 = 100) = 290 total revenue
            // Expected: order1 (2+3 = 5) + order2 (1) = 6 total items
            return sales.getReportDate().equals(testDate) &&
                   sales.getInvoicedOrdersCount() == 2 &&
                   sales.getInvoicedItemsCount() == 6 &&
                   sales.getTotalRevenue() == 290.0;
        }));
    }

    @Test
    public void testCalculateDailySales_SingleOrderMultipleItems() {
        // Given
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(Arrays.asList(order1));
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(order1Items);

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).insert(argThat(sales -> {
            // Expected: 2*50 + 3*30 = 190 total revenue
            // Expected: 2+3 = 5 total items
            return sales.getInvoicedOrdersCount() == 1 &&
                   sales.getInvoicedItemsCount() == 5 &&
                   sales.getTotalRevenue() == 190.0;
        }));
    }

    @Test
    public void testCalculateDailySales_MultipleOrdersSingleItem() {
        // Given
        OrderPojo order3 = TestData.orderPojo();
        order3.setId(3);
        
        List<OrderItemPojo> order3Items = Arrays.asList(
            TestData.orderItemPojo(4, 4, 2, 75.0)
        );

        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(Arrays.asList(order1, order2, order3));
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(order1Items);
        when(orderService.getOrderItemsByOrderId(2)).thenReturn(order2Items);
        when(orderService.getOrderItemsByOrderId(3)).thenReturn(order3Items);

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).insert(argThat(sales -> {
            // Expected: order1 (190) + order2 (100) + order3 (2*75=150) = 440 total revenue
            // Expected: order1 (5) + order2 (1) + order3 (2) = 8 total items
            return sales.getInvoicedOrdersCount() == 3 &&
                   sales.getInvoicedItemsCount() == 8 &&
                   sales.getTotalRevenue() == 440.0;
        }));
    }

    @Test
    public void testGetBetween_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        List<DaySalesPojo> expectedSales = Arrays.asList(
            TestData.daySales(start),
            TestData.daySales(end)
        );
        when(daySalesService.getBetween(start, end)).thenReturn(expectedSales);

        // When
        List<DaySalesPojo> result = daySalesFlow.getBetween(start, end);

        // Then
        assertEquals(expectedSales, result);
        verify(daySalesService, times(1)).getBetween(start, end);
    }

    @Test
    public void testCalculateDailySales_OrderWithNoItems() {
        // Given
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(Arrays.asList(order1));
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(new ArrayList<>());

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).insert(argThat(sales -> {
            return sales.getInvoicedOrdersCount() == 1 &&
                   sales.getInvoicedItemsCount() == 0 &&
                   sales.getTotalRevenue() == 0.0;
        }));
    }

    @Test
    public void testCalculateDailySales_ZeroQuantityItems() {
        // Given
        List<OrderItemPojo> zeroItems = Arrays.asList(
            TestData.orderItemPojo(1, 1, 0, 50.0)
        );
        
        when(daySalesService.getByDate(testDate)).thenReturn(null);
        when(orderService.getOrdersForSpecificDate(testDate)).thenReturn(Arrays.asList(order1));
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(zeroItems);

        // When
        daySalesFlow.calculateDailySales(testDate);

        // Then
        verify(daySalesService, times(1)).insert(argThat(sales -> {
            return sales.getInvoicedOrdersCount() == 1 &&
                   sales.getInvoicedItemsCount() == 0 &&
                   sales.getTotalRevenue() == 0.0;
        }));
    }
} 