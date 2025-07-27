package com.increff.pos.invoice.unit.flow;

import com.increff.pos.setup.TestData;
import com.increff.pos.flow.InvoiceFlow;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.pojo.InvoicePojo;
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
public class InvoiceFlowTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private InvoiceFlow invoiceFlow;

    private OrderPojo testOrder;
    private List<OrderItemPojo> testOrderItems;
    private InvoicePojo testInvoice;
    private ProductPojo testProduct;

    @Before
    public void setUp() {
        testOrder = TestData.orderPojo();
        testOrder.setId(1);

        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("INV-001");
        testProduct.setName("Invoice Test Product");

        OrderItemPojo item1 = TestData.orderItemPojo(1, 1, 2, 50.0);
        testOrderItems = Arrays.asList(item1);

        testInvoice = new InvoicePojo();
        testInvoice.setId(1);
        testInvoice.setOrderId(1);
        testInvoice.setFilePath("../invoices/order-1.pdf");
    }

    /**
     * Tests successful invoice generation for a given order.
     * Verifies calls to order and invoice services.
     */
    @Test
    public void testGenerateInvoice() {
        // Given
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(testOrderItems);
        when(invoiceService.createInvoiceRecord(testOrder, testOrderItems)).thenReturn(testInvoice);

        // When
        InvoicePojo result = invoiceFlow.generateInvoice(1);

        // Then
        assertNotNull(result);
        assertEquals(testInvoice, result);
        verify(orderService, times(1)).getCheckByOrderId(1);
        verify(orderService, times(1)).getOrderItemsByOrderId(1);
        verify(invoiceService, times(1)).createInvoiceRecord(testOrder, testOrderItems);
    }

    /**
     * Tests retrieving an invoice by its ID.
     * Verifies proper delegation to invoice service.
     */
    @Test
    public void testGetInvoiceById() {
        // Given
        when(invoiceService.getInvoiceById(1)).thenReturn(testInvoice);

        // When
        InvoicePojo result = invoiceFlow.getInvoiceById(1);

        // Then
        assertEquals(testInvoice, result);
        verify(invoiceService, times(1)).getInvoiceById(1);
    }

    /**
     * Tests retrieving order details for invoice generation.
     * Verifies proper order retrieval for invoice context.
     */
    @Test
    public void testGetOrderForInvoice() {
        // Given
        when(orderService.getCheckByOrderId(1)).thenReturn(testOrder);

        // When
        OrderPojo result = invoiceFlow.getOrderForInvoice(1);

        // Then
        assertEquals(testOrder, result);
        verify(orderService, times(1)).getCheckByOrderId(1);
    }

    /**
     * Tests retrieving order items for invoice generation.
     * Verifies proper order items collection for invoice processing.
     */
    @Test
    public void testGetOrderItemsForInvoice() {
        // Given
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(testOrderItems);

        // When
        List<OrderItemPojo> result = invoiceFlow.getOrderItemsForInvoice(1);

        // Then
        assertEquals(testOrderItems, result);
        verify(orderService, times(1)).getOrderItemsByOrderId(1);
    }
} 