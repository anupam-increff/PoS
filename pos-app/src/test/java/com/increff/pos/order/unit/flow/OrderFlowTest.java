package com.increff.pos.order.unit.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderFlowTest {

    @InjectMocks
    private OrderFlow flow;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private InventoryService inventoryService;

    @Test
    public void testPlaceOrder() throws ApiException {
        // Setup product
        ProductPojo product = new ProductPojo();
        product.setId(1);
        product.setName("Test Product");
        product.setMrp(100.0);
        when(productService.getCheckProductById(1)).thenReturn(product);

        // Setup order item
        OrderItemPojo item = new OrderItemPojo();
        item.setProductId(1);
        item.setQuantity(2);
        item.setSellingPrice(90.0);

        // Setup order
        OrderPojo order = new OrderPojo();
        order.setId(1);
        order.setOrderStatus(OrderStatus.CREATED);
        when(orderService.createOrderWithItems(anyList())).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(order);

        // Execute
        OrderPojo result = flow.placeOrder(Arrays.asList(item));

        // Verify
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getOrderStatus());
        verify(orderService).createOrderWithItems(anyList());
        verify(productService).validateSellingPrice(eq(90.0), eq(product));
        verify(inventoryService).validateSufficientAndReduceInventory(eq(1), eq(2), eq("Test Product"));
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrderInvalidSellingPrice() throws ApiException {
        // Setup product
        ProductPojo product = new ProductPojo();
        product.setId(1);
        product.setName("Test Product");
        product.setMrp(100.0);
        when(productService.getCheckProductById(1)).thenReturn(product);

        // Setup order item with selling price higher than MRP
        OrderItemPojo item = new OrderItemPojo();
        item.setProductId(1);
        item.setQuantity(2);
        item.setSellingPrice(110.0);

        // Setup validation to throw exception
        doThrow(new ApiException("Selling price cannot be greater than MRP"))
                .when(productService).validateSellingPrice(eq(110.0), eq(product));

        // Execute
        flow.placeOrder(Arrays.asList(item));
    }

    @Test(expected = ApiException.class)
    public void testPlaceOrderEmptyItems() throws ApiException {
        flow.placeOrder(Collections.emptyList());
    }

    @Test
    public void testPlaceOrderMultipleItems() throws ApiException {
        // Setup products
        ProductPojo product1 = new ProductPojo();
        product1.setId(1);
        product1.setName("Test Product 1");
        product1.setMrp(100.0);
        when(productService.getCheckProductById(1)).thenReturn(product1);

        ProductPojo product2 = new ProductPojo();
        product2.setId(2);
        product2.setName("Test Product 2");
        product2.setMrp(200.0);
        when(productService.getCheckProductById(2)).thenReturn(product2);

        // Setup order items
        OrderItemPojo item1 = new OrderItemPojo();
        item1.setProductId(1);
        item1.setQuantity(2);
        item1.setSellingPrice(90.0);

        OrderItemPojo item2 = new OrderItemPojo();
        item2.setProductId(2);
        item2.setQuantity(1);
        item2.setSellingPrice(180.0);

        // Setup order
        OrderPojo order = new OrderPojo();
        order.setId(1);
        order.setOrderStatus(OrderStatus.CREATED);
        when(orderService.createOrderWithItems(anyList())).thenReturn(1);
        when(orderService.getCheckByOrderId(1)).thenReturn(order);

        // Execute
        OrderPojo result = flow.placeOrder(Arrays.asList(item1, item2));

        // Verify
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getOrderStatus());
        verify(orderService).createOrderWithItems(anyList());
        verify(productService).validateSellingPrice(eq(90.0), eq(product1));
        verify(productService).validateSellingPrice(eq(180.0), eq(product2));
        verify(inventoryService).validateSufficientAndReduceInventory(eq(1), eq(2), eq("Test Product 1"));
        verify(inventoryService).validateSufficientAndReduceInventory(eq(2), eq(1), eq("Test Product 2"));
    }
} 