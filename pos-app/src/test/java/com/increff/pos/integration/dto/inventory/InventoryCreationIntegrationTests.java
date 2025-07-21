package com.increff.pos.integration.dto.inventory;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.TestData;
import com.increff.pos.util.TSVUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private MultipartFile createMockMultipartFile(List<InventoryForm> forms) {
        try {
            String[] headers = {"barcode", "quantity"};
            byte[] tsvBytes = TSVUtil.createTsvFromList(forms, headers);
            return new org.springframework.mock.web.MockMultipartFile(
                "file", "test.tsv", "text/tab-separated-values", tsvBytes
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock MultipartFile", e);
        }
    }

    @Test
    public void testAddInventory() throws ApiException {
        // Arrange - Create client and product using DAO
        String uniqueClientName = getUniqueClientName("Client-AddInventory");
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

        // Assert - Verify inventory was created
        PaginatedResponse<InventoryData> result = inventoryDto.getAll(0, 10);
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        
        boolean found = false;
        for (InventoryData data : result.getContent()) {
            if (uniqueBarcode.equals(data.getBarcode())) {
                assertEquals(100, data.getQuantity().intValue());
                found = true;
                break;
            }
        }
        assertTrue(found);

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

        // Act - Add more inventory (should add to existing quantity)
        inventoryDto.addInventory(form2);

        // Assert - Verify quantities were added together
        InventoryPojo dbInventory2 = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory2);
        assertEquals(300, dbInventory2.getQuantity().intValue()); // 100 + 200 = 300
    }

    @Test
    public void testGetAllInventory() throws ApiException {
        // Arrange - Create client and products using DAO
        String uniqueClientName = getUniqueClientName("Client-GetAllInventory");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        ProductPojo product1 = TestData.productWithoutId(uniqueBarcode1, "Test Product 1", client.getId());
        ProductPojo product2 = TestData.productWithoutId(uniqueBarcode2, "Test Product 2", client.getId());
        productDao.insert(product1);
        productDao.insert(product2);
        
        InventoryForm form1 = TestData.inventoryForm(uniqueBarcode1, 100);
        InventoryForm form2 = TestData.inventoryForm(uniqueBarcode2, 200);
        
        // Create inventory through DTO methods
        inventoryDto.addInventory(form1);
        inventoryDto.addInventory(form2);

        // Act
        PaginatedResponse<InventoryData> result = inventoryDto.getAll(0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertTrue(result.getTotalItems() >= 2);

        // Assert - Verify database state using DAO select method
        long dbCount = inventoryDao.countAll();
        assertTrue(dbCount >= 2);
    }

    @Test
    public void testSearchInventoryByBarcode() throws ApiException {
        // Arrange - Create client and product using DAO
        String uniqueClientName = getUniqueClientName("Client-SearchInventory");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        
        InventoryForm form = TestData.inventoryForm(uniqueBarcode, 100);
        
        // Create inventory through DTO method
        inventoryDto.addInventory(form);

        // Act
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("BARCODE", 0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        for (InventoryData data : result.getContent()) {
            assertTrue(data.getBarcode().toLowerCase().contains("barcode"));
        }

        // Assert - Verify database state using DAO select method
        long dbCount = inventoryDao.countByBarcodeSearch("BARCODE");
        assertTrue(dbCount >= 1);
    }

    @Test
    public void testUpdateInventoryByBarcode() throws ApiException {
        // Arrange - Create client and product using DAO
        String uniqueClientName = getUniqueClientName("Client-UpdateInventory");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        product = productDao.getByBarcode(uniqueBarcode);
        
        InventoryForm addForm = TestData.inventoryForm(uniqueBarcode, 100);
        InventoryForm updateForm = TestData.inventoryForm(uniqueBarcode, 200);

        // Act - Create inventory through DTO method
        inventoryDto.addInventory(addForm);
        
        // Assert - Verify initial inventory
        InventoryPojo dbInventory1 = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory1);
        assertEquals(100, dbInventory1.getQuantity().intValue());

        // Act - Update inventory through DTO method
        inventoryDto.updateInventoryByBarcode(updateForm.getBarcode(),updateForm);

        // Assert - Verify updated inventory
        InventoryPojo dbInventory2 = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory2);
        assertEquals(200, dbInventory2.getQuantity().intValue());
    }

    @Test
    public void testBulkUploadInventorySuccess() throws ApiException {
        // Arrange - Create client and products using DAO
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        ProductPojo product1 = TestData.productWithoutId(uniqueBarcode1, "Test Product 1", client.getId());
        ProductPojo product2 = TestData.productWithoutId(uniqueBarcode2, "Test Product 2", client.getId());
        productDao.insert(product1);
        productDao.insert(product2);
        
        List<InventoryForm> forms = Arrays.asList(
            TestData.inventoryForm(uniqueBarcode1, 100),
            TestData.inventoryForm(uniqueBarcode2, 200)
        );
        
        // Act - Create inventory through DTO bulk upload
        TSVUploadResponse result = inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
        
        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("All 2 inventory items updated successfully", result.getMessage());
        assertEquals(2, result.getSuccessRows());
        
        // Assert - Verify database state using DAO select methods
        product1 = productDao.getByBarcode(uniqueBarcode1);
        product2 = productDao.getByBarcode(uniqueBarcode2);
        InventoryPojo dbInventory1 = inventoryDao.getByProductId(product1.getId());
        InventoryPojo dbInventory2 = inventoryDao.getByProductId(product2.getId());
        assertNotNull(dbInventory1);
        assertNotNull(dbInventory2);
        assertEquals(100, dbInventory1.getQuantity().intValue());
        assertEquals(200, dbInventory2.getQuantity().intValue());
    }

    @Test
    public void testBulkUploadInventoryWithValidationErrors() throws ApiException {
        // Arrange - Create invalid forms
        List<InventoryForm> forms = Arrays.asList(
            TestData.inventoryForm("", 100), // Invalid barcode
            TestData.inventoryForm("BARCODE-2", null), // Invalid quantity
            TestData.inventoryForm("BARCODE-3", 0) // Invalid quantity (must be at least 1)
        );
        
        // Act
        TSVUploadResponse result = inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("TSV processing completed with errors"));
        assertEquals(3, result.getErrorRows());
        assertEquals(0, result.getSuccessRows());
        assertNotNull(result.getDownloadUrl());
    }

    @Test
    public void testBulkUploadInventoryWithProcessingErrors() throws ApiException {
        // Arrange - Create client and one product
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        ProductPojo product1 = TestData.productWithoutId(uniqueBarcode1, "Test Product 1", client.getId());
        productDao.insert(product1);
        
        List<InventoryForm> forms = Arrays.asList(
            TestData.inventoryForm(uniqueBarcode1, 100), // Valid
            TestData.inventoryForm("NONEXISTENT-BARCODE", 200) // Non-existent product
        );
        
        // Act
        TSVUploadResponse result = inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("TSV processing completed with errors"));
        assertEquals(1, result.getErrorRows());
        assertEquals(1, result.getSuccessRows());
        assertNotNull(result.getDownloadUrl());
        
        // Verify only the valid inventory was created
        product1 = productDao.getByBarcode(uniqueBarcode1);
        InventoryPojo dbInventory1 = inventoryDao.getByProductId(product1.getId());
        assertNotNull(dbInventory1);
        assertEquals(100, dbInventory1.getQuantity().intValue());
    }

    @Test
    public void testBulkUploadInventoryWithNegativeQuantity() throws ApiException {
        // Arrange - Create client and product
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        
        List<InventoryForm> forms = Arrays.asList(
            TestData.inventoryForm(uniqueBarcode, -100) // Negative quantity
        );
        
        // Act
        TSVUploadResponse result = inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("TSV processing completed with errors"));
        assertEquals(1, result.getErrorRows());
        assertEquals(0, result.getSuccessRows());
        assertNotNull(result.getDownloadUrl());
    }

    @Test
    public void testBulkUploadInventoryMaxRowsExceeded() throws ApiException {
        // Arrange - Create more than 5000 forms
        List<InventoryForm> forms = new ArrayList<>();
        for (int i = 0; i < 5001; i++) {
            forms.add(TestData.inventoryForm("BARCODE-" + i, 100));
        }
        
        // Act & Assert - Should throw ApiException
        try {
            inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
            fail("Should throw ApiException for exceeding max rows");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("Maximum 5000 rows allowed"));
            assertTrue(e.getMessage().contains("5001"));
        }
    }

    @Test
    public void testBulkUploadInventoryWithDuplicateEntries() throws ApiException {
        // Arrange - Create client and product
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductPojo product = TestData.productWithoutId(uniqueBarcode, "Test Product", client.getId());
        productDao.insert(product);
        
        // Create existing inventory
        InventoryPojo existingInventory = TestData.inventoryWithoutId(product.getId(), 50);
        inventoryDao.insert(existingInventory);
        
        List<InventoryForm> forms = Arrays.asList(
            TestData.inventoryForm(uniqueBarcode, 100) // Duplicate entry
        );
        
        // Act
        TSVUploadResponse result = inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
        
        // Assert - Should succeed and add quantities
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("All 1 inventory items updated successfully", result.getMessage());
        assertEquals(1, result.getSuccessRows());
        
        // Verify that quantities were added together (50 + 100 = 150)
        product = productDao.getByBarcode(uniqueBarcode);
        InventoryPojo dbInventory = inventoryDao.getByProductId(product.getId());
        assertNotNull(dbInventory);
        assertEquals(150, dbInventory.getQuantity().intValue());
    }

    @Test
    public void testBulkUploadInventoryWithMixedValidAndInvalid() throws ApiException {
        // Arrange - Create client and products
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        ProductPojo product1 = TestData.productWithoutId(uniqueBarcode1, "Test Product 1", client.getId());
        ProductPojo product2 = TestData.productWithoutId(uniqueBarcode2, "Test Product 2", client.getId());
        productDao.insert(product1);
        productDao.insert(product2);
        
        List<InventoryForm> forms = Arrays.asList(
            TestData.inventoryForm(uniqueBarcode1, 100), // Valid
            TestData.inventoryForm("", 200), // Invalid barcode
            TestData.inventoryForm(uniqueBarcode2, -50) // Invalid quantity
        );
        
        // Act
        TSVUploadResponse result = inventoryDto.uploadInventoryByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("TSV processing completed with errors"));
        assertEquals(2, result.getErrorRows());
        assertEquals(1, result.getSuccessRows());
        assertNotNull(result.getDownloadUrl());
    }
} 