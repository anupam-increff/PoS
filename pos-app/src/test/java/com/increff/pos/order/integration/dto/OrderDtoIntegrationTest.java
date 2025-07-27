package com.increff.pos.order.integration.dto;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.*;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Integration tests for OrderDto.
 * Tests integration between OrderDto -> OrderFlow -> OrderService/ProductService/InventoryService -> DAOs
 */
public class OrderDtoIntegrationTest extends AbstractTest {

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private InventoryDao inventoryDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;

    @Before
    public void setUp() {
        // Setup test data
        testClient = TestData.clientWithoutId("Test Client Order");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("ORDER-001", "Order Test Product", testClient.getId());
        productDao.insert(testProduct);

        // Setup inventory
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 100);
        inventoryDao.insert(inventory);
    }

    @Test
    public void testPlaceOrder_DtoFlowMultiServiceIntegration() {
        // Given
        OrderItemForm orderItemForm = TestData.orderItemForm("ORDER-001", 5, 95.0);
        OrderForm orderForm = TestData.orderForm(Arrays.asList(orderItemForm));

        // When - DTO orchestrates through Flow -> Multiple Services -> DAOs
        OrderData orderData = orderDto.placeOrder(orderForm);

        // Then - Verify multi-service integration worked
        assertNotNull("Order should be created through DTO integration", orderData);
        assertNotNull("Order ID should be generated", orderData.getId());
        
        OrderPojo savedOrder = orderDao.getById(orderData.getId());
        assertNotNull("Order should be saved through service integration", savedOrder);
        
        // Verify inventory was updated through service orchestration
        InventoryPojo updatedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertEquals("Inventory should be updated through service orchestration", 
            Integer.valueOf(95), updatedInventory.getQuantity());
    }

    @Test
    public void testGetAll_DtoServiceIntegration() {
        // Given - Create test order through services
        OrderPojo order = TestData.orderPojo();
        orderDao.insert(order);

        // When - DTO integrates with Service for pagination
        PaginatedResponse<OrderData> response = orderDto.getAll(0, 10);

        // Then - Verify DTO integration worked
        assertNotNull("Response should be provided by DTO->Service integration", response);
        assertTrue("Should contain at least one order", response.getContent().size() >= 1);
    }
} 