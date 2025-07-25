package com.increff.pos.unit.service;

import com.increff.pos.config.TestData;
import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.InvoiceService;
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

        testOrderItems = new ArrayList<>();
        testOrderItems.add(TestData.orderItemPojo(1, 1, 2, 99.99));
    }

    @Test
    public void testCheckIfInvoiceExistsForOrder_True() {
        // Given
        InvoicePojo invoice = new InvoicePojo();
        invoice.setId(1);
        invoice.setOrderId(1);
        when(invoiceDao.getByOrderId(1)).thenReturn(invoice);

        // When
        boolean exists = invoiceService.checkIfInvoiceExistsForOrder(1);

        // Then
        assertTrue(exists);
    }

    @Test
    public void testCheckIfInvoiceExistsForOrder_False() {
        // Given
        when(invoiceDao.getByOrderId(1)).thenReturn(null);

        // When
        boolean exists = invoiceService.checkIfInvoiceExistsForOrder(1);

        // Then
        assertFalse(exists);
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
} 