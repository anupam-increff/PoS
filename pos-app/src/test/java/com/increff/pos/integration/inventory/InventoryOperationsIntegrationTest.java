package com.increff.pos.integration.inventory;

import com.increff.pos.config.IntegrationTestConfig;
import com.increff.pos.config.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Integration tests for Inventory operations.
 * Tests inventory management including add, update, search, and validation scenarios.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestConfig.class})
@Transactional
public class InventoryOperationsIntegrationTest {

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
        // Create test client and product for inventory operations
        testClient = TestData.clientWithoutId("TestClient");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("BARCODE-001", "Test Product", testClient.getId());
        productDao.insert(testProduct);
    }

    @Test
    public void testAddInventory_Success() {
        // Given: Valid inventory form
        InventoryForm form = TestData.inventoryForm("BARCODE-001", 100);

        // When: Adding inventory
        inventoryDto.addInventory(form);

        // Then: Inventory should be saved in database
        InventoryPojo savedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Inventory should be saved", savedInventory);
        assertEquals("Quantity should match", Integer.valueOf(100), savedInventory.getQuantity());
        assertEquals("Product ID should match", testProduct.getId(), savedInventory.getProductId());
    }

    @Test(expected = ApiException.class)
    public void testAddInventory_NonExistentProduct() {
        // Given: Inventory form with non-existent product barcode
        InventoryForm form = TestData.inventoryForm("NON-EXISTENT", 50);

        // When: Adding inventory for non-existent product
        // Then: Should throw ApiException
        inventoryDto.addInventory(form);
    }

    @Test(expected = ApiException.class)
    public void testAddInventory_NegativeQuantity() {
        // Given: Inventory form with negative quantity
        InventoryForm form = TestData.inventoryForm("BARCODE-001", -10);

        // When: Adding inventory with negative quantity
        // Then: Should throw ApiException
        inventoryDto.addInventory(form);
    }

    @Test
    public void testUpdateInventory_Success() {
        // Given: Existing inventory
        InventoryForm initialForm = TestData.inventoryForm("BARCODE-001", 100);
        inventoryDto.addInventory(initialForm);

        // When: Updating inventory quantity
        InventoryForm updateForm = TestData.inventoryForm("BARCODE-001", 150);
        inventoryDto.updateInventoryByBarcode("BARCODE-001", updateForm);

        // Then: Inventory should be updated in database
        InventoryPojo updatedInventory = inventoryDao.getByProductId(testProduct.getId());
        assertNotNull("Inventory should exist", updatedInventory);
        assertEquals("Quantity should be updated", Integer.valueOf(150), updatedInventory.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testUpdateInventory_NonExistentProduct() {
        // Given: Update form for non-existent product
        InventoryForm form = TestData.inventoryForm("NON-EXISTENT", 50);

        // When: Updating inventory for non-existent product
        // Then: Should throw ApiException
        inventoryDto.updateInventoryByBarcode("NON-EXISTENT", form);
    }

    @Test
    public void testGetAllInventory() {
        // Given: Multiple inventory entries
        ProductPojo product2 = TestData.productWithoutId("BARCODE-002", "Product 2", testClient.getId());
        productDao.insert(product2);

        inventoryDto.addInventory(TestData.inventoryForm("BARCODE-001", 100));
        inventoryDto.addInventory(TestData.inventoryForm("BARCODE-002", 200));

        // When: Getting all inventory
        PaginatedResponse<InventoryData> response = inventoryDto.getAll(0, 10);

        // Then: Should return all inventory with product details
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 2 inventory entries", 2, response.getContent().size());
        assertEquals("Total items should be 2", 2, response.getTotalItems());

        // Verify inventory data includes product information
        InventoryData inventoryData = response.getContent().get(0);
        assertNotNull("Barcode should be populated", inventoryData.getBarcode());
        assertNotNull("Product name should be populated", inventoryData.getName());
    }

    @Test
    public void testSearchInventoryByBarcode() {
        // Given: Multiple inventory entries
        ProductPojo product2 = TestData.productWithoutId("BARCODE-002", "Product 2", testClient.getId());
        ProductPojo product3 = TestData.productWithoutId("ITEM-003", "Product 3", testClient.getId());
        productDao.insert(product2);
        productDao.insert(product3);

        inventoryDto.addInventory(TestData.inventoryForm("BARCODE-001", 100));
        inventoryDto.addInventory(TestData.inventoryForm("BARCODE-002", 200));
        inventoryDto.addInventory(TestData.inventoryForm("ITEM-003", 300));

        // When: Searching for inventory with "BARCODE"
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("BARCODE", 0, 10);

        // Then: Should return inventory entries matching barcode pattern
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 2 inventory entries", 2, response.getContent().size());
        
        // Verify all returned items contain "BARCODE"
        response.getContent().forEach(inventory -> 
            assertTrue("Barcode should contain 'BARCODE'", 
                inventory.getBarcode().contains("BARCODE")));
    }

    @Test
    public void testInventoryWithPagination() {
        // Given: Multiple inventory entries for pagination test
        for (int i = 1; i <= 10; i++) {
            String barcode = "TEST-" + String.format("%03d", i);
            ProductPojo product = TestData.productWithoutId(barcode, "Product " + i, testClient.getId());
            product.setMrp(100.0);
            productDao.insert(product);
            inventoryDto.addInventory(TestData.inventoryForm(barcode, i * 10));
        }

        // When: Getting first page with size 4
        PaginatedResponse<InventoryData> page1 = inventoryDto.getAll(0, 4);

        // Then: Should return correct pagination info
        assertNotNull("Page 1 should not be null", page1);
        assertEquals("Page 1 should have 4 entries", 4, page1.getContent().size());
        assertEquals("Total items should be 10", 10, page1.getTotalItems());
        assertEquals("Should have 3 total pages", 3, page1.getTotalPages());

        // When: Getting second page
        PaginatedResponse<InventoryData> page2 = inventoryDto.getAll(1, 4);

        // Then: Should return different inventory entries
        assertNotNull("Page 2 should not be null", page2);
        assertEquals("Page 2 should have 4 entries", 4, page2.getContent().size());
        
        // Verify pages have different content
        assertNotEquals("Pages should have different content",
            page1.getContent().get(0).getBarcode(),
            page2.getContent().get(0).getBarcode());
    }

    @Test
    public void testSearchInventoryNotFound() {
        // Given: Some inventory exists
        inventoryDto.addInventory(TestData.inventoryForm("BARCODE-001", 100));

        // When: Searching for non-matching pattern
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("NONEXISTENT", 0, 10);

        // Then: Should return empty result
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 0 inventory entries", 0, response.getContent().size());
        assertEquals("Total items should be 0", 0, response.getTotalItems());
    }
} 