package com.increff.pos.order.unit.dto;

import com.increff.pos.setup.TestData;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
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
public class OrderDtoTest {

    @Mock
    private OrderFlow orderFlow;

    @Mock
    private ProductService productService;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private OrderDto orderDto;

    private OrderPojo testOrder;
    private OrderForm testOrderForm;
    private ProductPojo testProduct;
    private OrderItemForm testOrderItemForm;

    @Before
    public void setUp() {
        testOrder = TestData.orderPojo();
        testOrder.setId(1);

        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("ORDER-001");

        testOrderItemForm = TestData.orderItemForm("ORDER-001", 2, 95.0);
        testOrderForm = TestData.orderForm(Arrays.asList(testOrderItemForm));
    }

    /**
     * Tests placing an order through the DTO layer.
     * Verifies proper form validation and flow integration.
     */
    @Test
    public void testPlaceOrder() {
        // Given
        when(productService.getCheckProductByBarcode("ORDER-001")).thenReturn(testProduct);
        when(orderFlow.placeOrder(any())).thenReturn(testOrder);

        // When
        OrderData result = orderDto.placeOrder(testOrderForm);

        // Then
        assertNotNull("Order data should not be null", result);
        assertEquals("Order ID should match", testOrder.getId(), result.getId());
        verify(orderFlow, times(1)).placeOrder(any());
    }

    /**
     * Tests retrieving all orders with pagination through DTO.
     * Verifies proper pagination and data conversion.
     */
    @Test
    public void testGetAll() {
        // Given
        List<OrderPojo> orders = Arrays.asList(testOrder);
        when(orderFlow.getAllOrders(0, 10)).thenReturn(orders);
        when(orderFlow.countAllOrders()).thenReturn(1L);

        // When
        PaginatedResponse<OrderData> result = orderDto.getAll(0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one order", 1, result.getContent().size());
        assertEquals("Total items should match", 1L, result.getTotalItems());
        verify(orderFlow, times(1)).getAllOrders(0, 10);
        verify(orderFlow, times(1)).countAllOrders();
    }
} 