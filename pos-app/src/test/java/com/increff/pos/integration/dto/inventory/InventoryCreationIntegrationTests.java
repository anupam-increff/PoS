package com.increff.pos.integration.dto.inventory;

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
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.increff.pos.setup.IntegrationTestConfig.class})
@Transactional
public class InventoryCreationIntegrationTests {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private static int testCounter = 0;

    @Before
    public void setUp() {
        // Clean up any existing data
        // This will be handled by @Transactional rollback
    }

    private String getUniqueClientName(String baseName) {
        return baseName + "_" + System.currentTimeMillis() + "_" + (++testCounter);
    }

    private String getUniqueBarcode(String baseBarcode) {
        return baseBarcode + "_" + System.currentTimeMillis() + "_" + (++testCounter);
    }

    @Test
    public void testAddInventory() throws ApiException {
        // Arrange - Create client and product using DAO
        String uniqueClientName = getUniqueClientName("Client-Inventory");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        product = productDao.getByBarcode(uniqueBarcode);
        
        InventoryForm form = TestData.inventoryForm(uniqueBarcode, 100);

        // Act - Create inventory through DTO method
        inventoryDto.addInventory(form);

        // Assert - Verify inventory was created by retrieving it through search
        PaginatedResponse<InventoryData> searchResult = inventoryDto.searchByBarcode(uniqueBarcode, 0, 10);
        assertNotNull(searchResult);
        assertTrue(searchResult.getContent().size() >= 1);
        InventoryData result = searchResult.getContent().get(0);
        assertEquals(100, result.getQuantity().intValue());
        assertEquals(uniqueBarcode, result.getBarcode());

        // Assert - Verify database state using DAO select method
        InventoryPojo dbInventory = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory);
        assertEquals(100, dbInventory.getQuantity().intValue());
        assertEquals(product.getId(), dbInventory.getProductId());
    }

    @Test
    public void testAddInventoryWithNonExistentProduct() {
        // Arrange
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        InventoryForm form = TestData.inventoryForm(uniqueBarcode, 100);

        // Act & Assert
        try {
            inventoryDto.addInventory(form);
            fail("Should throw ApiException for non-existent product");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("product"));
        }
    }

    @Test
    public void testAddInventoryWithDuplicateBarcode() throws ApiException {
        // Arrange - Create client and product using DAO
        String uniqueClientName = getUniqueClientName("Client-DuplicateInventory");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        product = productDao.getByBarcode(uniqueBarcode);
        
        InventoryForm form1 = TestData.inventoryForm(uniqueBarcode, 100);
        InventoryForm form2 = TestData.inventoryForm(uniqueBarcode, 200);

        // Act - Create first inventory through DTO method
        inventoryDto.addInventory(form1);

        // Assert - Verify first inventory was created
        InventoryPojo dbInventory1 = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory1);
        assertEquals(100, dbInventory1.getQuantity().intValue());

        // Act & Assert - Try to create duplicate
        try {
            inventoryDto.addInventory(form2);
            fail("Should throw ApiException for duplicate barcode");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("barcode"));
        }

        // Assert - Verify only one inventory exists in database
        InventoryPojo dbInventory2 = inventoryDao.getByProductId(product.getId());
        assertEquals(100, dbInventory2.getQuantity().intValue()); // Should still be the first inventory
    }

    @Test
    public void testGetInventoryByBarcode() throws ApiException {
        // Arrange - Create client and product using DAO
        String uniqueClientName = getUniqueClientName("Client-GetInventory");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        product = productDao.getByBarcode(uniqueBarcode);
        
        InventoryForm form = TestData.inventoryForm(uniqueBarcode, 100);
        
        // Create inventory through DTO method
        inventoryDto.addInventory(form);

        // Act - Get inventory through search
        PaginatedResponse<InventoryData> searchResult = inventoryDto.searchByBarcode(uniqueBarcode, 0, 10);
        assertNotNull(searchResult);
        assertTrue(searchResult.getContent().size() >= 1);
        InventoryData result = searchResult.getContent().get(0);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertEquals(100, result.getQuantity().intValue());
        assertEquals(uniqueBarcode, result.getBarcode());

        // Assert - Verify database state using DAO select method
        InventoryPojo dbInventory = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory);
        assertEquals(100, dbInventory.getQuantity().intValue());
        assertEquals(product.getId(), dbInventory.getProductId());
    }

    @Test
    public void testGetInventoryByBarcodeNotFound() {
        // Act & Assert
        try {
            PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("NONEXISTENT", 0, 10);
            assertEquals(0, result.getContent().size());
        } catch (ApiException e) {
            // This is expected behavior
        }
    }
} 