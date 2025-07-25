package com.increff.pos.unit.service;

import com.increff.pos.config.TestData;
import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderService;
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

    private InvoicePojo testInvoice;
    private OrderPojo testOrder;
    private List<OrderItemPojo> testOrderItems;

    @Before
    public void setUp() {
        testInvoice = TestData.invoicePojo(1, "../invoices/order-1.pdf");
        testInvoice.setId(1);
        
        testOrder = TestData.orderPojo();
        testOrder.setId(1);
        testOrder.setOrderStatus(OrderStatus.CREATED);
        
        testOrderItems = new ArrayList<>();
        testOrderItems.add(TestData.orderItemPojo(1, 1, 2, 99.99));
    }

    @Test
    public void testCheckIfInvoiceExistsForOrder_Exists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(testInvoice);

        // When
        boolean exists = invoiceService.checkIfInvoiceExistsForOrder(1);

        // Then
        assertTrue(exists);
        verify(invoiceDao, times(1)).getByOrderId(1);
    }

    @Test
    public void testCheckIfInvoiceExistsForOrder_NotExists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null);

        // When
        boolean exists = invoiceService.checkIfInvoiceExistsForOrder(1);

        // Then
        assertFalse(exists);
        verify(invoiceDao, times(1)).getByOrderId(1);
    }

    @Test
    public void testGetInvoiceIdByOrderId_Exists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(testInvoice);

        // When
        Integer invoiceId = invoiceService.getInvoiceIdByOrderId(1);

        // Then
        assertEquals(Integer.valueOf(1), invoiceId);
        verify(invoiceDao, times(2)).getByOrderId(1); // Called twice - once in check, once in get
    }

    @Test
    public void testGetInvoiceIdByOrderId_NotExists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null);

        // When
        Integer invoiceId = invoiceService.getInvoiceIdByOrderId(1);

        // Then
        assertNull(invoiceId);
        verify(invoiceDao, times(1)).getByOrderId(1);
    }

    @Test
    public void testCreateInvoiceRecord_Success() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null); // No existing invoice
        doAnswer(invocation -> {
            InvoicePojo invoice = invocation.getArgument(0);
            invoice.setId(1);
            return null;
        }).when(invoiceDao).insert(any(InvoicePojo.class));

        // When
        InvoicePojo result = invoiceService.createInvoiceRecord(testOrder, testOrderItems);

        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getOrderId());
        assertEquals("../invoices/order-1.pdf", result.getFilePath());
        
        verify(invoiceDao, times(1)).insert(any(InvoicePojo.class));
        verify(orderService, times(1)).updateOrderStatus(1, OrderStatus.INVOICE_GENERATED);
    }

    @Test(expected = ApiException.class)
    public void testCreateInvoiceRecord_InvoiceAlreadyExists() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(testInvoice);

        // When
        invoiceService.createInvoiceRecord(testOrder, testOrderItems);

        // Then - exception should be thrown
    }

    @Test
    public void testGetInvoiceById_Success() {
        // Given
        when(invoiceDao.getById(1)).thenReturn(testInvoice);

        // When
        InvoicePojo result = invoiceService.getInvoiceById(1);

        // Then
        assertEquals(testInvoice, result);
        verify(invoiceDao, times(1)).getById(1);
    }

    @Test(expected = ApiException.class)
    public void testGetInvoiceById_NotFound() {
        // Given
        when(invoiceDao.getById(1)).thenReturn(null);

        // When
        invoiceService.getInvoiceById(1);

        // Then - exception should be thrown
    }

    @Test
    public void testBuildInvoiceFilePath() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null); // No existing invoice
        doAnswer(invocation -> {
            InvoicePojo invoice = invocation.getArgument(0);
            invoice.setId(1);
            return null;
        }).when(invoiceDao).insert(any(InvoicePojo.class));

        // When
        InvoicePojo result = invoiceService.createInvoiceRecord(testOrder, testOrderItems);

        // Then
        assertTrue(result.getFilePath().contains("order-1"));
        assertTrue(result.getFilePath().endsWith(".pdf"));
    }
} 