package com.increff.pos.invoice.integration.dto;

import com.increff.pos.dao.*;
import com.increff.pos.dto.InvoiceDto;
import com.increff.pos.pojo.*;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

/**
 * Integration tests for InvoiceDto.
 * Tests integration between InvoiceDto -> InvoiceFlow -> InvoiceService/OrderService -> DAOs
 */
public class InvoiceDtoIntegrationTest extends AbstractTest {

    @Autowired
    private InvoiceDto invoiceDto;

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;
    private OrderPojo testOrder;

    @Before
    public void setUp() {
        // Setup test data
        testClient = TestData.clientWithoutId("Test Client Invoice");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("INVOICE-001", "Invoice Test Product", testClient.getId());
        productDao.insert(testProduct);

        // Create test order
        testOrder = TestData.orderPojo();
        orderDao.insert(testOrder);

        // Create test order items
        OrderItemPojo orderItem = TestData.orderItemWithoutId(testOrder.getId(), testProduct.getId(), 2, 50.0);
        orderItemDao.insert(orderItem);
    }

    /**
     * Tests invoice generation through the complete stack.
     * Verifies proper integration between DTO, Flow, Service and DAO layers.
     */
    @Test
    public void testGenerateInvoice() {
        // Given
        Integer orderId = testOrder.getId();

        // When - DTO integrates through Flow -> Services -> DAOs
        Integer invoiceId = invoiceDto.generateInvoice(orderId);

        // Then - Verify integration worked
        assertNotNull("Invoice ID should be returned", invoiceId);
        InvoicePojo savedInvoice = invoiceDao.getByOrderId(orderId);
        assertNotNull("Invoice should be generated through DTO->Flow->Service->DAO integration", savedInvoice);
        assertEquals("Invoice should be linked to correct order", orderId, savedInvoice.getOrderId());
        assertTrue("Invoice file path should be set", savedInvoice.getFilePath().contains("order-" + orderId));
    }

    /**
     * Tests downloading an invoice by its ID.
     * Verifies proper file handling and data retrieval through all layers.
     */
    @Test
    public void testDownloadInvoiceById() {
        // Given - Create invoice first and generate actual file
        Integer invoiceId = invoiceDto.generateInvoice(testOrder.getId());

        // When - DTO integrates with Flow and Service to get invoice
        ResponseEntity<byte[]> response = invoiceDto.downloadInvoiceById(invoiceId);

        // Then - Verify DTO integration worked
        assertNotNull("Response should be retrieved through DTO integration", response);
        assertNotNull("Invoice bytes should be in response body", response.getBody());
        assertTrue("Invoice should contain content", response.getBody().length > 0);
        assertEquals("Content type should be PDF", MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    }
} 