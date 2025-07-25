package com.increff.pos.unit.flow;

import com.increff.pos.config.TestData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
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
public class OrderFlowTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderFlow orderFlow;

    private List<OrderItemPojo> testOrderItems;
    private ProductPojo testProduct1, testProduct2;
    private OrderPojo testOrder;

    @Before
    public void setUp() {
        testProduct1 = TestData.product(1, 1);
        testProduct1.setMrp(100.0);
        testProduct2 = TestData.product(2, 1);
        testProduct2.setMrp(200.0);

        testOrderItems = Arrays.asList(
            TestData.orderItemPojo(1, 1, 2, 95.0),
            TestData.orderItemPojo(2, 2, 1, 180.0)
        );

        testOrder = TestData.orderPojo();
        testOrder.setId(1);
    }

    @Test
    public void testPlaceOrder_Success() {
        // Given
        when(productService.getCheckProductById(1)).thenReturn(testProduct1);
        when(productService.getCheckProductById(2)).thenReturn(testProduct2);
        when(inventoryService.getCheckByProductId(1)).thenReturn(TestData.inventory(1, 1));
        when(inventoryService.getCheckByProductId(2)).thenReturn(TestData.inventory(2, 2));
        when(orderService.createOrderWithItems(any())).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);

        // When
        OrderPojo result = orderFlow.placeOrder(testOrderItems);

        // Then
        assertNotNull(result);
        assertEquals(testOrder, result);
        
        verify(productService, times(1)).getCheckProductById(1);
        verify(productService, times(1)).getCheckProductById(2);
        verify(inventoryService, times(1)).getCheckByProductId(1);
        verify(inventoryService, times(1)).getCheckByProductId(2);
        verify(orderService, times(1)).createOrderWithItems(testOrderItems);
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrder_SellingPriceExceedsMrp() {
        // Given
        OrderItemPojo invalidItem = TestData.orderItemPojo(1, 1, 1, 150.0); // Price > MRP
        when(productService.getCheckProductById(1)).thenReturn(testProduct1);

        // When
        orderFlow.placeOrder(Arrays.asList(invalidItem));

        // Then - exception should be thrown
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrder_InsufficientInventory() {
        // Given
        when(productService.getCheckProductById(1)).thenReturn(testProduct1);
        when(inventoryService.getCheckByProductId(1))
            .thenReturn(TestData.inventoryWithoutId(1, 1)); // Insufficient quantity

        // When
        orderFlow.placeOrder(testOrderItems);

        // Then - exception should be thrown
    }

    @Test
    public void testGetAllOrders_Success() {
        // Given
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderService.getAllOrdersPaginated(0, 10)).thenReturn(orders);

        // When
        List<OrderPojo> result = orderFlow.getAllOrders(0, 10);

        // Then
        assertEquals(orders, result);
        verify(orderService, times(1)).getAllOrdersPaginated(0, 10);
    }

    @Test
    public void testCountAllOrders_Success() {
        // Given
        when(orderService.countAll()).thenReturn(5L);

        // When
        long count = orderFlow.countAllOrders();

        // Then
        assertEquals(5L, count);
        verify(orderService, times(1)).countAll();
    }

    @Test
    public void testSearchOrders_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderService.searchOrderByQuery(start, end, "test", 0, 10)).thenReturn(orders);

        // When
        List<OrderPojo> result = orderFlow.searchOrders(start, end, "test", 0, 10);

        // Then
        assertEquals(orders, result);
        verify(orderService, times(1)).searchOrderByQuery(start, end, "test", 0, 10);
    }

    @Test
    public void testCountMatchingOrders_Success() {
        // Given
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        when(orderService.countMatchingOrdersByQuery(start, end, "test")).thenReturn(3L);

        // When
        long count = orderFlow.countMatchingOrders(start, end, "test");

        // Then
        assertEquals(3L, count);
        verify(orderService, times(1)).countMatchingOrdersByQuery(start, end, "test");
    }

    @Test
    public void testValidateSellingPriceAgainstMrp_Valid() {
        // Given
        when(productService.getCheckProductById(1)).thenReturn(testProduct1);
        when(productService.getCheckProductById(2)).thenReturn(testProduct2);
        when(inventoryService.getCheckByProductId(1)).thenReturn(TestData.inventory(1, 1));
        when(inventoryService.getCheckByProductId(2)).thenReturn(TestData.inventory(2, 2));
        when(orderService.createOrderWithItems(any())).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);

        // When - Should not throw exception
        OrderPojo result = orderFlow.placeOrder(testOrderItems);

        // Then
        assertNotNull(result);
    }

    @Test
    public void testValidateSufficientInventory_Success() {
        // Given
        when(productService.getCheckProductById(1)).thenReturn(testProduct1);
        when(productService.getCheckProductById(2)).thenReturn(testProduct2);
        when(inventoryService.getCheckByProductId(1)).thenReturn(TestData.inventory(1, 1));
        when(inventoryService.getCheckByProductId(2)).thenReturn(TestData.inventory(2, 2));
        when(orderService.createOrderWithItems(any())).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);

        // When
        OrderPojo result = orderFlow.placeOrder(testOrderItems);

        // Then
        verify(inventoryService, times(2)).getCheckByProductId(anyInt());
        assertNotNull(result);
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrder_ProductNotFound() {
        // Given
        when(productService.getCheckProductById(1))
            .thenThrow(new ApiException("Product not found"));

        // When
        orderFlow.placeOrder(testOrderItems);

        // Then - exception should be thrown
    }

    @Test
    public void testPlaceOrder_EmptyOrderItems() {
        // Given
        when(orderService.createOrderWithItems(any())).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);

        // When
        OrderPojo result = orderFlow.placeOrder(Arrays.asList());

        // Then
        assertNotNull(result);
        verify(productService, never()).getCheckProductById(anyInt());
        verify(inventoryService, never()).getCheckByProductId(anyInt());
    }
} 