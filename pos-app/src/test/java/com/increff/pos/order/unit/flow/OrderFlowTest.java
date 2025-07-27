package com.increff.pos.order.unit.flow;

import com.increff.pos.setup.TestData;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.InventoryPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    private ProductPojo testProduct;
    private OrderPojo testOrder;

    @Before
    public void setUp() {
        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("ORDER-001");

        testOrder = TestData.orderPojo();
        testOrder.setId(1);

        OrderItemPojo item1 = TestData.orderItemPojo(1, 1, 2, 50.0);
        testOrderItems = Arrays.asList(item1);
    }

    @Test
    public void testPlaceOrder_Success() {
        // Given
        ProductPojo mockProduct = TestData.product(1, 1);
        mockProduct.setMrp(100.0);
        InventoryPojo mockInventory = TestData.inventoryWithoutId(1, 10);
        
        when(productService.getCheckProductById(1)).thenReturn(mockProduct);
        when(inventoryService.getCheckByProductId(1)).thenReturn(mockInventory);
        when(orderService.createOrderWithItems(testOrderItems)).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);

        // When
        OrderPojo result = orderFlow.placeOrder(testOrderItems);

        // Then
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(productService, times(1)).getCheckProductById(1);
        verify(inventoryService, times(1)).getCheckByProductId(1);
        verify(orderService, times(1)).createOrderWithItems(testOrderItems);
        verify(orderService, times(1)).getCheckByOrderId(1);
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
} 