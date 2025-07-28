package com.increff.pos.inventory.integration.flow;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Integration tests for InventoryFlow.
 * Tests the complete flow from InventoryFlow -> Services -> DAOs with real database operations.
 */
public class InventoryFlowIntegrationTest extends AbstractTest {

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;

    @Before
    public void setUp() {
        // Setup test data in database
        testClient = TestData.clientWithoutId("Integration Test Client");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("INV-FLOW-INT-001", "Integration Test Product", testClient.getId());
        productDao.insert(testProduct);
    }

    /**
     * Tests adding new inventory through the flow layer with database persistence.
     * Verifies that inventory is correctly created and stored.
     */
    @Test
    public void testAddInventory() {
        // When - Add inventory through Flow
        inventoryFlow.addInventory("INV-FLOW-INT-001", 50);

        // Then - Verify database state through DAO
        InventoryPojo dbInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Inventory should be created in database", dbInventory);
        assertEquals("Quantity should match", Integer.valueOf(50), dbInventory.getQuantity());
        assertEquals("Product ID should match", testProduct.getId(), dbInventory.getProductId());
    }

    /**
     * Tests updating existing inventory quantities through the flow layer.
     * Verifies that existing records are properly modified.
     */
    @Test
    public void testUpdateInventory() {
        // Given - Create initial inventory
        InventoryPojo initialInventory = TestData.inventoryWithoutId(testProduct.getId(), 30);
        inventoryDao.insert(initialInventory);

        // When - Update through Flow
        inventoryFlow.updateInventory("INV-FLOW-INT-001", 75);

        // Then - Verify database update
        InventoryPojo updatedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Inventory should exist in database", updatedInventory);
        assertEquals("Quantity should be updated", Integer.valueOf(75), updatedInventory.getQuantity());
    }

    /**
     * Tests retrieving all inventory items with complete data population.
     * Verifies the flow layer properly aggregates data from multiple sources.
     */
    @Test
    public void testGetAllInventory() {
        // Given - Create inventory in database
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 25);
        inventoryDao.insert(inventory);

        // When - Get all through Flow
        PaginatedResponse<InventoryData> result = inventoryFlow.getAll(0, 10);

        // Then - Verify integration worked
        assertNotNull("Result should not be null", result);
        assertTrue("Should contain at least one inventory item", result.getContent().size() >= 1);
        
        // Find our test inventory
        InventoryData foundItem = result.getContent().stream()
            .filter(item -> "INV-FLOW-INT-001".equals(item.getBarcode()))
            .findFirst()
            .orElse(null);
            
        assertNotNull("Our test inventory should be found", foundItem);
        assertEquals("Barcode should match", "INV-FLOW-INT-001", foundItem.getBarcode());
        assertEquals("Product name should match", "Integration Test Product", foundItem.getName());
        assertEquals("Quantity should match", Integer.valueOf(25), foundItem.getQuantity());
    }

    /**
     * Tests searching inventory by barcode pattern through the flow layer.
     * Verifies search functionality works with database queries and data transformation.
     */
    @Test
    public void testSearchInventoryByBarcode() {
        // Given - Create inventory in database
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 40);
        inventoryDao.insert(inventory);

        // When - Search by barcode through Flow
        PaginatedResponse<InventoryData> result = inventoryFlow.searchByBarcode("INV-FLOW-INT", 0, 10);

        // Then - Verify search worked through full integration
        assertNotNull("Search result should not be null", result);
        assertEquals("Should find exactly one matching item", 1, result.getContent().size());
        
        InventoryData foundItem = result.getContent().get(0);
        assertEquals("Barcode should match search", "INV-FLOW-INT-001", foundItem.getBarcode());
        assertEquals("Product name should be populated", "Integration Test Product", foundItem.getName());
        assertEquals("Quantity should match", Integer.valueOf(40), foundItem.getQuantity());
    }

    /**
     * Tests behavior when retrieving inventory from an empty database.
     * Verifies proper handling of no-data scenarios.
     */
    @Test
    public void testGetAllInventoryEmptyDatabase() {
        // When - Get all from empty database
        PaginatedResponse<InventoryData> result = inventoryFlow.getAll(0, 10);

        // Then - Should handle empty case properly
        assertNotNull("Result should not be null even when empty", result);
        assertEquals("Should return empty list", 0, result.getContent().size());
        assertEquals("Total items should be 0", 0L, result.getTotalItems());
    }
} 