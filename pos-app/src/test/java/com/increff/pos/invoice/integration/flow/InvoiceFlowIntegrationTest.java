package com.increff.pos.invoice.integration.flow;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.*;
import com.increff.pos.flow.InvoiceFlow;
import com.increff.pos.pojo.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for InvoiceFlow.
 * Tests the complete flow from InvoiceFlow -> Services -> DAOs with real database operations.
 */
public class InvoiceFlowIntegrationTest extends AbstractTest {

    @Autowired
    private InvoiceFlow invoiceFlow;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private InvoiceDao invoiceDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;
    private OrderPojo testOrder;

    @Before
    public void setUp() {
        // Setup test data in database
        testClient = TestData.clientWithoutId("Invoice Flow Client");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("INV-FLOW-001", "Invoice Flow Product", testClient.getId());
        testProduct.setMrp(100.0);
        productDao.insert(testProduct);

        testOrder = TestData.orderWithoutId();
        orderDao.insert(testOrder);

        OrderItemPojo orderItem = TestData.orderItemWithoutId(testOrder.getId(), testProduct.getId(), 2, 90.0);
        orderItemDao.insert(orderItem);
    }

    /**
     * Tests generating an invoice through the complete flow layer.
     * Verifies invoice creation, file generation, and database persistence.
     */
    @Test
    public void testGenerateInvoice() {
        // When - Generate invoice through Flow
        InvoicePojo generatedInvoice = invoiceFlow.generateInvoice(testOrder.getId());

        // Then - Verify invoice was created
        assertNotNull("Invoice should be generated", generatedInvoice);
        assertNotNull("Invoice should have ID", generatedInvoice.getId());
        assertEquals("Invoice should be linked to order", testOrder.getId(), generatedInvoice.getOrderId());
        assertNotNull("Invoice should have file path", generatedInvoice.getFilePath());
        assertTrue("File path should contain order ID", generatedInvoice.getFilePath().contains("order-" + testOrder.getId()));

        // Verify invoice exists in database
        InvoicePojo dbInvoice = invoiceDao.getById(generatedInvoice.getId());
        assertNotNull("Invoice should exist in database", dbInvoice);
        assertEquals("Database invoice should match generated", generatedInvoice.getId(), dbInvoice.getId());
        assertEquals("Order ID should match in database", testOrder.getId(), dbInvoice.getOrderId());
    }

    /**
     * Tests retrieving an invoice by ID through the flow layer.
     * Verifies proper invoice lookup and data consistency.
     */
    @Test
    public void testGetInvoiceById() {
        // Given - Create invoice in database first
        InvoicePojo createdInvoice = invoiceFlow.generateInvoice(testOrder.getId());

        // When - Get invoice by ID through Flow
        InvoicePojo retrievedInvoice = invoiceFlow.getInvoiceById(createdInvoice.getId());

        // Then - Verify integration worked
        assertNotNull("Retrieved invoice should not be null", retrievedInvoice);
        assertEquals("Invoice ID should match", createdInvoice.getId(), retrievedInvoice.getId());
        assertEquals("Order ID should match", testOrder.getId(), retrievedInvoice.getOrderId());
        assertEquals("File path should match", createdInvoice.getFilePath(), retrievedInvoice.getFilePath());
    }

    /**
     * Tests retrieving order information for invoice generation.
     * Verifies the flow layer correctly fetches order details.
     */
    @Test
    public void testGetOrderForInvoice() {
        // When - Get order for invoice through Flow
        OrderPojo retrievedOrder = invoiceFlow.getOrderForInvoice(testOrder.getId());

        // Then - Verify integration worked
        assertNotNull("Retrieved order should not be null", retrievedOrder);
        assertEquals("Order ID should match", testOrder.getId(), retrievedOrder.getId());
        
        // Verify this is the same order from database
        OrderPojo dbOrder = orderDao.getById(testOrder.getId());
        assertEquals("Should retrieve same order as in database", dbOrder.getId(), retrievedOrder.getId());
        assertEquals("Order timestamps should match", dbOrder.getCreatedAt(), retrievedOrder.getCreatedAt());
    }

    /**
     * Tests retrieving order items for invoice generation.
     * Verifies proper order item lookup and data population.
     */
    @Test
    public void testGetOrderItemsForInvoice() {
        // When - Get order items for invoice through Flow
        List<OrderItemPojo> retrievedItems = invoiceFlow.getOrderItemsForInvoice(testOrder.getId());

        // Then - Verify integration worked
        assertNotNull("Retrieved items should not be null", retrievedItems);
        assertEquals("Should have one order item", 1, retrievedItems.size());
        
        OrderItemPojo item = retrievedItems.get(0);
        assertEquals("Order ID should match", testOrder.getId(), item.getOrderId());
        assertEquals("Product ID should match", testProduct.getId(), item.getProductId());
        assertEquals("Quantity should match", Integer.valueOf(2), item.getQuantity());
        assertEquals("Selling price should match", Double.valueOf(90.0), item.getSellingPrice());

        // Verify this matches database directly
        List<OrderItemPojo> dbItems = orderItemDao.getByOrderId(testOrder.getId());
        assertEquals("Should match database count", dbItems.size(), retrievedItems.size());
        assertEquals("First item should match database", dbItems.get(0).getId(), item.getId());
    }

    /**
     * Tests preventing duplicate invoice generation for the same order.
     * Verifies proper validation and error handling.
     */
    @Test
    public void testGenerateInvoiceDuplicate() {
        // Given - Generate first invoice
        InvoicePojo firstInvoice = invoiceFlow.generateInvoice(testOrder.getId());
        assertNotNull("First invoice should be created", firstInvoice);

        // When & Then - Try to generate second invoice for same order
        try {
            invoiceFlow.generateInvoice(testOrder.getId());
            fail("Should not allow duplicate invoice generation");
        } catch (Exception e) {
            // Expected - Flow should prevent duplicate invoices
            assertTrue("Exception should mention duplicate or existing invoice", 
                e.getMessage().toLowerCase().contains("invoice") ||
                e.getMessage().toLowerCase().contains("exists") ||
                e.getMessage().toLowerCase().contains("already"));
        }

        // Verify only one invoice exists
        InvoicePojo dbInvoice = invoiceDao.getByOrderId(testOrder.getId());
        assertEquals("Should have original invoice ID", firstInvoice.getId(), dbInvoice.getId());
    }
} 