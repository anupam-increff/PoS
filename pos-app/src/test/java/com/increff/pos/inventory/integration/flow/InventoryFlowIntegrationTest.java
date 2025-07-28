package com.increff.pos.inventory.integration.flow;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        List<InventoryPojo> result = inventoryFlow.getAll(0, 10);

        // Then - Verify results
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one inventory", 1, result.size());
        assertEquals("Product ID should match", testProduct.getId(), result.get(0).getProductId());
        assertEquals("Quantity should match", Integer.valueOf(25), result.get(0).getQuantity());
    }

    /**
     * Tests searching inventory by barcode pattern.
     * Verifies proper search functionality and data aggregation.
     */
    @Test
    public void testSearchInventoryByBarcode() {
        // Given - Create inventory in database
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 25);
        inventoryDao.insert(inventory);

        // When - Search through Flow
        List<InventoryPojo> result = inventoryFlow.searchByBarcode("INV-FLOW", 0, 10);

        // Then - Verify results
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one inventory", 1, result.size());
        assertEquals("Product ID should match", testProduct.getId(), result.get(0).getProductId());
        assertEquals("Quantity should match", Integer.valueOf(25), result.get(0).getQuantity());
    }

    /**
     * Tests retrieving all inventory when database is empty.
     * Verifies proper handling of empty results.
     */
    @Test
    public void testGetAllInventoryEmptyDatabase() {
        // When - Get all from empty database
        List<InventoryPojo> result = inventoryFlow.getAll(0, 10);

        // Then - Verify empty results
        assertNotNull("Result should not be null", result);
        assertEquals("Should be empty", 0, result.size());
    }
} 