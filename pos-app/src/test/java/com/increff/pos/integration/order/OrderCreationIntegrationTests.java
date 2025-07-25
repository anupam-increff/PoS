package com.increff.pos.integration.order;

import com.increff.pos.config.IntegrationTestConfig;
import com.increff.pos.config.TestData;
import com.increff.pos.dao.*;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for Order DTO operations.
 * Tests order creation, item management, search, and validation scenarios.
 * These tests verify the full flow from DTO through to database persistence.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestConfig.class)
@Transactional
public class OrderCreationIntegrationTests {

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private InventoryDao inventoryDao;

    private ClientPojo testClient;
    private ProductPojo testProduct1;
    private ProductPojo testProduct2;

    @Before
    public void setUp() {
        // Create test client and products for order operations
        testClient = TestData.clientWithoutId("TestClient");
        clientDao.insert(testClient);

        testProduct1 = TestData.productWithoutId("BARCODE-001", "Product 1", testClient.getId());
        testProduct1.setMrp(100.0);
        productDao.insert(testProduct1);

        testProduct2 = TestData.productWithoutId("BARCODE-002", "Product 2", testClient.getId());
        testProduct2.setMrp(200.0);
        productDao.insert(testProduct2);

        // Add inventory for products
        InventoryPojo inventory1 = TestData.inventoryWithoutId(testProduct1.getId(), 100);
        InventoryPojo inventory2 = TestData.inventoryWithoutId(testProduct2.getId(), 50);
        inventoryDao.insert(inventory1);
        inventoryDao.insert(inventory2);
    }

    @Test
    public void testCreateOrder_Success() {
        // Given: Valid order form with items
        OrderItemForm item1 = TestData.orderItemForm("BARCODE-001", 5, 95.0);
        OrderItemForm item2 = TestData.orderItemForm("BARCODE-002", 3, 180.0);
        OrderForm orderForm = TestData.orderForm(Arrays.asList(item1, item2));

        // When: Creating order
        OrderData orderData = orderDto.placeOrder(orderForm);

        // Then: Order should be created with correct details
        assertNotNull("Order data should not be null", orderData);
        assertNotNull("Order ID should not be null", orderData.getId());
        
        OrderPojo savedOrder = orderDao.getById(orderData.getId());
        assertNotNull("Order should be saved", savedOrder);
        assertEquals("Order status should be CREATED", OrderStatus.CREATED, savedOrder.getOrderStatus());

        // Verify order items are saved
        List<OrderItemPojo> orderItems = orderItemDao.getByOrderId(orderData.getId());
        assertEquals("Should have 2 order items", 2, orderItems.size());
    }

    @Test(expected = ApiException.class)
    public void testCreateOrder_InsufficientInventory() {
        // Given: Order item with quantity exceeding available inventory
        OrderItemForm item = TestData.orderItemForm("BARCODE-001", 150, 95.0); // Only 100 available
        OrderForm orderForm = TestData.orderForm(Arrays.asList(item));

        // When: Creating order with insufficient inventory
        // Then: Should throw ApiException
        orderDto.placeOrder(orderForm);
    }

    @Test(expected = ApiException.class)
    public void testCreateOrder_NonExistentProduct() {
        // Given: Order item with non-existent product
        OrderItemForm item = TestData.orderItemForm("NON-EXISTENT", 5, 100.0);
        OrderForm orderForm = TestData.orderForm(Arrays.asList(item));

        // When: Creating order with non-existent product
        // Then: Should throw ApiException
        orderDto.placeOrder(orderForm);
    }

    @Test(expected = ApiException.class)
    public void testCreateOrder_InvalidSellingPrice() {
        // Given: Order item with selling price higher than MRP
        OrderItemForm item = TestData.orderItemForm("BARCODE-001", 5, 150.0); // MRP is 100.0
        OrderForm orderForm = TestData.orderForm(Arrays.asList(item));

        // When: Creating order with invalid selling price
        // Then: Should throw ApiException
        orderDto.placeOrder(orderForm);
    }

    @Test
    public void testGetOrderItems() {
        // Given: Order with multiple items
        OrderItemForm item1 = TestData.orderItemForm("BARCODE-001", 5, 95.0);
        OrderItemForm item2 = TestData.orderItemForm("BARCODE-002", 3, 180.0);
        OrderForm orderForm = TestData.orderForm(Arrays.asList(item1, item2));
        OrderData orderData = orderDto.placeOrder(orderForm);

        // When: Getting order items
        List<OrderItemData> orderItems = orderDto.getItemsByOrderId(orderData.getId());

        // Then: Should return all order items with product details
        assertEquals("Should have 2 order items", 2, orderItems.size());
        
        OrderItemData firstItem = orderItems.get(0);
        assertNotNull("Order item should have barcode", firstItem.getBarcode());
        assertNotNull("Order item should have product name", firstItem.getProductName());
        assertTrue("Barcode should be valid", firstItem.getBarcode().startsWith("BARCODE-"));
    }

    @Test
    public void testGetAllOrders() {
        // Given: Multiple orders exist
        createTestOrder("BARCODE-001", 2, 95.0);
        createTestOrder("BARCODE-002", 1, 180.0);

        // When: Getting all orders
        PaginatedResponse<OrderData> response = orderDto.getAll(0, 10);

        // Then: Should return all orders
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 2 orders", 2, response.getContent().size());
        assertEquals("Total items should be 2", 2, response.getTotalItems());

        // Verify order data is populated
        OrderData orderData = response.getContent().get(0);
        assertNotNull("Order ID should be set", orderData.getId());
        assertNotNull("Order status should be set", orderData.getOrderStatus());
        assertNotNull("Placed date should be set", orderData.getPlacedAt());
    }

    @Test
    public void testOrdersWithPagination() {
        // Given: Multiple orders for pagination test
        for (int i = 1; i <= 8; i++) {
            createTestOrder("BARCODE-001", 1, 95.0);
        }

        // When: Getting first page with size 3
        PaginatedResponse<OrderData> page1 = orderDto.getAll(0, 3);

        // Then: Should return correct pagination info
        assertNotNull("Page 1 should not be null", page1);
        assertEquals("Page 1 should have 3 orders", 3, page1.getContent().size());
        assertEquals("Total items should be 8", 8, page1.getTotalItems());
        assertEquals("Should have 3 total pages", 3, page1.getTotalPages());

        // When: Getting second page
        PaginatedResponse<OrderData> page2 = orderDto.getAll(1, 3);

        // Then: Should return different orders
        assertNotNull("Page 2 should not be null", page2);
        assertEquals("Page 2 should have 3 orders", 3, page2.getContent().size());
        
        // Verify pages have different content (different order IDs)
        assertNotEquals("Pages should have different content",
            page1.getContent().get(0).getId(),
            page2.getContent().get(0).getId());
    }

    @Test
    public void testOrderSearch() {
        // Given: Orders created at different times
        Integer order1 = createTestOrder("BARCODE-001", 2, 95.0);
        Integer order2 = createTestOrder("BARCODE-002", 1, 180.0);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime yesterday = now.minusDays(1);
        ZonedDateTime tomorrow = now.plusDays(1);

        // When: Searching orders in date range
        PaginatedResponse<OrderData> response = orderDto.searchOrders(yesterday, tomorrow, null, 0, 10);

        // Then: Should return orders within date range
        assertNotNull("Response should not be null", response);
        assertTrue("Should have orders", response.getContent().size() >= 2);
        assertEquals("Total should include created orders", 2, response.getTotalItems());
    }

    private Integer createTestOrder(String barcode, int quantity, double sellingPrice) {
        OrderItemForm item = TestData.orderItemForm(barcode, quantity, sellingPrice);
        OrderForm orderForm = TestData.orderForm(Arrays.asList(item));
        OrderData orderData = orderDto.placeOrder(orderForm);
        return orderData.getId();
    }
} 