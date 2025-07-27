package com.increff.pos.order.integration.flow;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.*;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.pojo.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for OrderFlow.
 * Tests the complete flow from OrderFlow -> Services -> DAOs with real database operations.
 */
public class OrderFlowIntegrationTest extends AbstractTest {

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;
    private InventoryPojo testInventory;

    @Before
    public void setUp() {
        // Setup test data in database
        testClient = TestData.clientWithoutId("Order Flow Client");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("ORDER-FLOW-001", "Order Flow Product", testClient.getId());
        testProduct.setMrp(100.0);
        productDao.insert(testProduct);

        testInventory = TestData.inventoryWithoutId(testProduct.getId(), 50);
        inventoryDao.insert(testInventory);
    }

    /**
     * Tests placing an order through the complete flow with inventory validation.
     * Verifies multi-service integration including inventory reduction.
     */
    @Test
    public void testPlaceOrder() {
        // Given - Order items to place
        OrderItemPojo orderItem = TestData.orderItemWithoutId(0, testProduct.getId(), 3, 90.0);
        List<OrderItemPojo> orderItems = Arrays.asList(orderItem);

        // When - Place order through Flow
        OrderPojo placedOrder = orderFlow.placeOrder(orderItems);

        // Then - Verify order was created in database
        assertNotNull("Order should be created", placedOrder);
        assertNotNull("Order should have ID", placedOrder.getId());

        // Verify order exists in database
        OrderPojo dbOrder = orderDao.getById(placedOrder.getId());
        assertNotNull("Order should exist in database", dbOrder);

        // Verify order items were created
        List<OrderItemPojo> dbOrderItems = orderItemDao.getByOrderId(placedOrder.getId());
        assertEquals("Should have one order item", 1, dbOrderItems.size());
        assertEquals("Product ID should match", testProduct.getId(), dbOrderItems.get(0).getProductId());
        assertEquals("Quantity should match", Integer.valueOf(3), dbOrderItems.get(0).getQuantity());

        // Verify inventory was reduced
        InventoryPojo updatedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertEquals("Inventory should be reduced", Integer.valueOf(47), updatedInventory.getQuantity());
    }

    /**
     * Tests retrieving all orders with proper pagination.
     * Verifies flow layer correctly fetches and formats order data.
     */
    @Test
    public void testGetAllOrders() {
        // Given - Create some orders in database
        OrderPojo order1 = TestData.orderWithoutId();
        orderDao.insert(order1);
        
        OrderPojo order2 = TestData.orderWithoutId();
        orderDao.insert(order2);

        // When - Get all orders through Flow
        List<OrderPojo> orders = orderFlow.getAllOrders(0, 10);

        // Then - Verify integration worked
        assertNotNull("Orders should not be null", orders);
        assertTrue("Should contain at least 2 orders", orders.size() >= 2);

        // Verify our test orders are included
        boolean foundOrder1 = orders.stream().anyMatch(o -> o.getId().equals(order1.getId()));
        boolean foundOrder2 = orders.stream().anyMatch(o -> o.getId().equals(order2.getId()));
        assertTrue("Order 1 should be found", foundOrder1);
        assertTrue("Order 2 should be found", foundOrder2);
    }

    /**
     * Tests counting total orders through the flow layer.
     * Verifies accurate count retrieval from database.
     */
    @Test
    public void testCountAllOrders() {
        // Given - Create orders in database
        OrderPojo order1 = TestData.orderWithoutId();
        orderDao.insert(order1);
        
        OrderPojo order2 = TestData.orderWithoutId();
        orderDao.insert(order2);

        // When - Count all orders through Flow
        long count = orderFlow.countAllOrders();

        // Then - Verify count includes our test orders
        assertTrue("Count should be at least 2", count >= 2);
    }

    /**
     * Tests order placement validation when inventory is insufficient.
     * Verifies proper error handling and inventory protection.
     */
    @Test
    public void testPlaceOrderInsufficientInventory() {
        // Given - Order items with quantity exceeding inventory
        OrderItemPojo orderItem = TestData.orderItemWithoutId(0, testProduct.getId(), 100, 90.0); // More than available 50
        List<OrderItemPojo> orderItems = Arrays.asList(orderItem);

        // When & Then - Should throw exception due to insufficient inventory
        try {
            orderFlow.placeOrder(orderItems);
            fail("Should have thrown exception for insufficient inventory");
        } catch (Exception e) {
            // Expected - Flow should validate inventory through services
            assertTrue("Exception message should contain inventory info", 
                e.getMessage().toLowerCase().contains("inventory") || 
                e.getMessage().toLowerCase().contains("quantity"));
        }

        // Verify inventory wasn't modified
        InventoryPojo unchangedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertEquals("Inventory should remain unchanged", Integer.valueOf(50), unchangedInventory.getQuantity());
    }
} 