package com.increff.pos.inventory.integration.dto;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.InventoryPojo;
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

    @Test
    public void testAddInventory_DtoFlowServiceDaoIntegration() {
        // Given
        InventoryForm inventoryForm = TestData.inventoryForm("INV-001", 100);

        // When - DTO integrates through Flow -> Service -> DAO
        inventoryDto.addInventory(inventoryForm);

        // Then - Verify integration worked
        InventoryPojo savedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Inventory should be saved through DTO->Flow->Service->DAO integration", savedInventory);
        assertEquals("Inventory quantity should match", Integer.valueOf(100), savedInventory.getQuantity());
        assertEquals("Product ID should match", testProduct.getId(), savedInventory.getProductId());
    }

    @Test
    public void testGetAll_DtoFlowServiceIntegration() {
        // Given - Setup test inventory
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 50);
        inventoryDao.insert(inventory);

        // When - DTO integrates with Flow and Service for pagination
        PaginatedResponse<InventoryData> response = inventoryDto.getAll(0, 10);

        // Then - Verify DTO integration worked
        assertNotNull("Response should be provided by DTO integration", response);
        assertTrue("Should contain at least one inventory item", response.getContent().size() >= 1);
        
        InventoryData inventoryData = response.getContent().stream()
            .filter(i -> i.getBarcode().equals("INV-001"))
            .findFirst()
            .orElse(null);
        
        assertNotNull("Should find inventory for our test product", inventoryData);
        assertEquals("Quantity should match", Integer.valueOf(50), inventoryData.getQuantity());
    }

    @Test
    public void testUpdateInventory_DtoFlowServiceDaoIntegration() {
        // Given - Setup existing inventory
        InventoryPojo existingInventory = TestData.inventoryWithoutId(testProduct.getId(), 30);
        inventoryDao.insert(existingInventory);

        InventoryForm updateForm = TestData.inventoryForm("INV-001", 80);

        // When - DTO integrates through Flow -> Service -> DAO for update
        inventoryDto.updateInventoryByBarcode("INV-001", updateForm);

        // Then - Verify integration worked
        InventoryPojo updatedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Updated inventory should exist", updatedInventory);
        assertEquals("Quantity should be updated through service integration", 
            Integer.valueOf(80), updatedInventory.getQuantity());
    }

    @Test
    public void testSearchByBarcode_DtoFlowServiceIntegration() {
        // Given - Setup test inventory
        InventoryPojo inventory = TestData.inventoryWithoutId(testProduct.getId(), 25);
        inventoryDao.insert(inventory);

        // When - DTO integrates with Flow for search
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("INV", 0, 10);

        // Then - Verify search integration
        assertNotNull("Search results should be provided by DTO integration", response);
        assertTrue("Should find matching inventory items", response.getContent().size() >= 1);
        
        boolean foundMatch = response.getContent().stream()
            .anyMatch(i -> i.getBarcode().contains("INV"));
        assertTrue("Should contain items matching search criteria", foundMatch);
    }
} 