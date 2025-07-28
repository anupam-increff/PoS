package com.increff.pos.inventory.integration.dto;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
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
 * Integration tests for InventoryDto.
 * Tests integration between InventoryDto -> InventoryFlow -> InventoryService/ProductService -> DAOs
 */
public class InventoryDtoIntegrationTest extends AbstractTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;

    @Before
    public void setUp() {
        // Setup test data
        testClient = TestData.clientWithoutId("Test Client Inventory");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("INV-001", "Inventory Test Product", testClient.getId());
        productDao.insert(testProduct);
    }

    /**
     * Tests adding inventory through the complete integration stack.
     * Verifies DTO → Flow → Service → DAO integration with database persistence.
     */
    @Test
    public void testAddInventory() {
        // Given
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode("INV-001"); // Use the correct product barcode from setUp
        inventoryForm.setQuantity(50);

        // When - Add inventory through DTO
        inventoryDto.addInventory(inventoryForm);

        // Then - Verify database state
        InventoryPojo dbInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Inventory should be persisted in database", dbInventory);
        assertEquals("Quantity should match", Integer.valueOf(50), dbInventory.getQuantity());
    }

    /**
     * Tests updating inventory quantity through complete integration.
     * Verifies the full flow updates existing inventory records correctly.
     */
    @Test
    public void testUpdateInventory() {
        // Given - Create initial inventory
        InventoryPojo initialInventory = TestData.inventoryWithoutId(testProduct.getId(), 30);
        inventoryDao.insert(initialInventory);
        
        InventoryForm updateForm = new InventoryForm();
        updateForm.setBarcode("INV-001"); // Use the correct product barcode from setUp
        updateForm.setQuantity(75);

        // When - Update through DTO
        inventoryDto.updateInventoryByBarcode("INV-001", updateForm);

        // Then - Verify update in database
        InventoryPojo updatedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertEquals("Quantity should be updated", Integer.valueOf(75), updatedInventory.getQuantity());
    }

    /**
     * Tests retrieving all inventory items with pagination.
     * Verifies the complete integration returns properly formatted data.
     */
    @Test
    public void testGetAllInventory() {
        // Given - Create inventory in database
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 25);
        inventoryDao.insert(inventory);

        // When - Retrieve through DTO
        PaginatedResponse<InventoryData> response = inventoryDto.getAll(0, 10);

        // Then - Verify integration results
        assertNotNull("Response should not be null", response);
        assertTrue("Should contain inventory items", response.getContent().size() >= 1);
        
        InventoryData foundItem = response.getContent().stream()
            .filter(item -> "INV-001".equals(item.getBarcode()))
            .findFirst()
            .orElse(null);
            
        assertNotNull("Test inventory should be found", foundItem);
        assertEquals("Product name should be populated", "Inventory Test Product", foundItem.getName());
    }

    /**
     * Tests searching inventory by barcode through the integration stack.
     * Verifies search functionality works end-to-end with database queries.
     */
    @Test
    public void testSearchInventoryByBarcode() {
        // Given - Create searchable inventory
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 40);
        inventoryDao.insert(inventory);

        // When - Search through DTO
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("INV", 0, 10);

        // Then - Verify search results
        assertNotNull("Search results should not be null", response);
        assertEquals("Should find one matching item", 1, response.getContent().size());
        
        InventoryData foundItem = response.getContent().get(0);
        assertEquals("Barcode should match search", "INV-001", foundItem.getBarcode());
        assertEquals("Quantity should be correct", Integer.valueOf(40), foundItem.getQuantity());
    }
} 