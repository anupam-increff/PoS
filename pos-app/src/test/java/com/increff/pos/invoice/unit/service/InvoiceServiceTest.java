package com.increff.pos.invoice.unit.service;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceTest {

    @Mock
    private InvoiceDao invoiceDao;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private InvoiceService invoiceService;

    private OrderPojo testOrder;
    private List<OrderItemPojo> testOrderItems;
    private InvoicePojo testInvoice;

    @Before
    public void setUp() {
        testOrder = TestData.orderPojo();
        testOrder.setId(1);
        
        testOrderItems = new ArrayList<>();
        testOrderItems.add(TestData.orderItemPojo(1, 1, 2, 10.0));
        
        testInvoice = new InvoicePojo();
        testInvoice.setId(1);
        testInvoice.setOrderId(1);
        testInvoice.setFilePath("../invoices/order-1.pdf");
    }

    /**
     * Tests retrieving an invoice by its ID.
     * Verifies proper invoice retrieval and error handling.
     */
    @Test
    public void testGetInvoiceById() {
        // Given
        when(invoiceDao.getById(1)).thenReturn(testInvoice);

        // When
        InvoicePojo result = invoiceService.getInvoiceById(1);

        // Then
        assertNotNull("Invoice should not be null", result);
        assertEquals("Invoice should match", testInvoice, result);
        verify(invoiceDao, times(1)).getById(1);
    }

    /**
     * Tests error handling when invoice ID is not found.
     */
    @Test(expected = ApiException.class)
    public void testGetInvoiceByIdNotFound() {
        // Given
        when(invoiceDao.getById(1)).thenReturn(null);

        // When
        invoiceService.getInvoiceById(1);

        // Then - exception should be thrown
    }

    /**
     * Tests verification that invoice exists for an order.
     */
    @Test
    public void testCheckIfInvoiceExistsForOrderTrue() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(testInvoice);

        // When
        boolean exists = invoiceService.checkIfInvoiceExistsForOrder(1);

        // Then
        assertTrue(exists);
        verify(invoiceDao, times(1)).getByOrderId(1);
    }

    /**
     * Tests verification that invoice does not exist for an order.
     */
    @Test
    public void testCheckIfInvoiceExistsForOrderFalse() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null);

        // When
        boolean exists = invoiceService.checkIfInvoiceExistsForOrder(1);

        // Then
        assertFalse(exists);
        verify(invoiceDao, times(1)).getByOrderId(1);
    }

    /**
     * Tests retrieving invoice ID for an existing order.
     */
    @Test
    public void testGetInvoiceIdByOrderIdExists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(testInvoice);

        // When
        Integer invoiceId = invoiceService.getInvoiceIdByOrderId(1);

        // Then
        assertEquals(Integer.valueOf(1), invoiceId);
        // Note: getByOrderId is called twice - once in checkIfInvoiceExistsForOrder, once in getInvoiceIdByOrderId
        verify(invoiceDao, times(2)).getByOrderId(1);
    }

    /**
     * Tests error handling when invoice ID is not found for order.
     */
    @Test
    public void testGetInvoiceIdByOrderIdNotExists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null);

        // When
        Integer invoiceId = invoiceService.getInvoiceIdByOrderId(1);

        // Then
        assertNull(invoiceId);
        verify(invoiceDao, times(1)).getByOrderId(1);
    }

    /**
     * Tests creating an invoice record for an order.
     * Verifies invoice creation and order status update.
     */
    @Test
    public void testCreateInvoiceRecord() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null);
        doAnswer(invocation -> {
            InvoicePojo invoice = invocation.getArgument(0);
            invoice.setId(1); // Simulate ID generation
            return null;
        }).when(invoiceDao).insert(any(InvoicePojo.class));
        doNothing().when(orderService).updateOrderStatus(anyInt(), any());

        // When
        InvoicePojo result = invoiceService.createInvoiceRecord(testOrder, testOrderItems);

        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getOrderId());
        assertTrue(result.getFilePath().contains("order-1.pdf"));
        verify(invoiceDao, times(1)).getByOrderId(1);
        verify(invoiceDao, times(1)).insert(any(InvoicePojo.class));
        verify(orderService, times(1)).updateOrderStatus(eq(1), eq(OrderStatus.INVOICE_GENERATED));
    }

    /**
     * Tests error handling when creating duplicate invoice record.
     */
    @Test(expected = ApiException.class)
    public void testCreateInvoiceRecordAlreadyExists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(testInvoice);

        // When
        invoiceService.createInvoiceRecord(testOrder, testOrderItems);

        // Then - exception should be thrown
    }
} 