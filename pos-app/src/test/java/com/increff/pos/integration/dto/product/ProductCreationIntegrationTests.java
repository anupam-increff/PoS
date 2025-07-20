package com.increff.pos.integration.dto.product;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.Assert.*;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.util.TSVUtil;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.increff.pos.setup.IntegrationTestConfig.class})
@Transactional
public class ProductCreationIntegrationTests {

    @Autowired
    private ProductDto productDto;

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

    private MultipartFile createMockMultipartFile(List<ProductForm> forms) {
        try {
            byte[] tsvBytes = TSVUtil.createTsvFromList(forms, ProductForm.class);
            return new org.springframework.mock.web.MockMultipartFile(
                "file", "test.tsv", "text/tab-separated-values", tsvBytes
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock MultipartFile", e);
        }
    }

    @Test
    public void testAddProduct() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("Client-AddProduct");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductForm form = TestData.productForm(uniqueBarcode, "Test Product", client.getName(), 99.99);

        // Act - Create product through DTO method
        productDto.addProduct(form);

        // Assert - Verify product was created by retrieving it
        ProductData result = productDto.getByBarcode(uniqueBarcode);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(uniqueBarcode, result.getBarcode());
        assertEquals(99.99, result.getMrp(), 0.01);
        assertEquals(client.getName(), result.getClientName());

        // Assert - Verify database state using DAO select method
        ProductPojo dbProduct = productDao.getByBarcode(uniqueBarcode);
        assertNotNull(dbProduct);
        assertEquals("Test Product", dbProduct.getName());
        assertEquals(99.99, dbProduct.getMrp(), 0.01);
        assertEquals(client.getId(), dbProduct.getClientId());
    }

    @Test
    public void testAddProductWithNonExistentClient() {
        // Arrange
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductForm form = TestData.productForm(uniqueBarcode, "Test Product", "NonExistentClient", 99.99);

        // Act & Assert
        try {
            productDto.addProduct(form);
            fail("Should throw ApiException for non-existent client");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("Client"));
        }
    }

    @Test
    public void testAddProductWithDuplicateBarcode() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("Client-Duplicate");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductForm form1 = TestData.productForm(uniqueBarcode, "Product 1", client.getName(), 99.99);
        ProductForm form2 = TestData.productForm(uniqueBarcode, "Product 2", client.getName(), 88.88);

        // Act - Create first product through DTO method
        productDto.addProduct(form1);

        // Assert - Verify first product was created
        ProductPojo dbProduct1 = productDao.getByBarcode(uniqueBarcode);
        assertNotNull(dbProduct1);
        assertEquals("Product 1", dbProduct1.getName());

        // Act & Assert - Try to create duplicate
        try {
            productDto.addProduct(form2);
            fail("Should throw ApiException for duplicate barcode");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("barcode"));
        }

        // Assert - Verify only one product exists in database
        ProductPojo dbProduct2 = productDao.getByBarcode(uniqueBarcode);
        assertEquals("Product 1", dbProduct2.getName()); // Should still be the first product
    }

    @Test
    public void testGetAllProducts() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("Client-GetAll");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        ProductForm form1 = TestData.productForm(uniqueBarcode1, "Product 1", client.getName(), 99.99);
        ProductForm form2 = TestData.productForm(uniqueBarcode2, "Product 2", client.getName(), 88.88);
        
        // Create products through DTO methods
        productDto.addProduct(form1);
        productDto.addProduct(form2);

        // Act
        PaginatedResponse<ProductData> result = productDto.getAll(0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertTrue(result.getTotalItems() >= 2);

        // Assert - Verify database state using DAO select method
        long dbCount = productDao.countAll();
        assertTrue(dbCount >= 2);
        
        List<ProductPojo> dbProducts = productDao.getAllProducts(0, 10);
        assertTrue(dbProducts.size() >= 2);
    }

    @Test
    public void testGetProductsByClient() throws ApiException {
        // Arrange - Create clients using DAO
        String uniqueClientName1 = getUniqueClientName("Client-ByClient-1");
        String uniqueClientName2 = getUniqueClientName("Client-ByClient-2");
        ClientPojo client1 = TestData.clientWithoutId(uniqueClientName1);
        ClientPojo client2 = TestData.clientWithoutId(uniqueClientName2);
        clientDao.insert(client1);
        clientDao.insert(client2);
        client1 = clientDao.getClientByName(uniqueClientName1);
        client2 = clientDao.getClientByName(uniqueClientName2);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        String uniqueBarcode3 = getUniqueBarcode("BARCODE-3");
        ProductForm form1 = TestData.productForm(uniqueBarcode1, "Product 1", client1.getName(), 99.99);
        ProductForm form2 = TestData.productForm(uniqueBarcode2, "Product 2", client1.getName(), 88.88);
        ProductForm form3 = TestData.productForm(uniqueBarcode3, "Product 3", client2.getName(), 77.77);
        
        // Create products through DTO methods
        productDto.addProduct(form1);
        productDto.addProduct(form2);
        productDto.addProduct(form3);

        // Act
        PaginatedResponse<ProductData> result = productDto.getByClient(client1.getName(), 0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        for (ProductData product : result.getContent()) {
            assertEquals(client1.getName(), product.getClientName());
        }

        // Assert - Verify database state using DAO select method
        long dbCount = productDao.countByClientId(client1.getId());
        assertEquals(2, dbCount);
        
        List<ProductPojo> dbProducts = productDao.getProductsByClientId(client1.getId(), 0, 10);
        assertEquals(2, dbProducts.size());
    }

    @Test
    public void testSearchProductsByBarcode() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("Client-SearchBarcode");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        ProductForm form1 = TestData.productForm(uniqueBarcode1, "Product 1", client.getName(), 99.99);
        ProductForm form2 = TestData.productForm(uniqueBarcode2, "Product 2", client.getName(), 88.88);
        
        // Create products through DTO methods
        productDto.addProduct(form1);
        productDto.addProduct(form2);

        // Act
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("BARCODE", 0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
        for (ProductData product : result.getContent()) {
            assertTrue(product.getBarcode().toLowerCase().contains("barcode"));
        }

        // Assert - Verify database state using DAO select method
        long dbCount = productDao.countByBarcodeSearch("BARCODE");
        assertTrue(dbCount >= 2);
        
        List<ProductPojo> dbProducts = productDao.searchByBarcode("BARCODE", 0, 10);
        assertTrue(dbProducts.size() >= 2);
    }

    @Test
    public void testGetProductByBarcode() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("Client-GetByBarcode");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        ProductForm form = TestData.productForm(uniqueBarcode, "Test Product", client.getName(), 99.99);
        
        // Create product through DTO method
        productDto.addProduct(form);

        // Act
        ProductData result = productDto.getByBarcode(uniqueBarcode);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(uniqueBarcode, result.getBarcode());

        // Assert - Verify database state using DAO select method
        ProductPojo dbProduct = productDao.getByBarcode(uniqueBarcode);
        assertNotNull(dbProduct);
        assertEquals("Test Product", dbProduct.getName());
        assertEquals(uniqueBarcode, dbProduct.getBarcode());
    }

    @Test
    public void testGetProductByBarcodeNotFound() {
        // Act & Assert
        try {
            productDto.getByBarcode("NONEXISTENT");
            fail("Should throw ApiException for non-existent product");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("product"));
        }
    }

    @Test
    public void testBulkUploadProductsSuccess() throws ApiException {
        // Arrange - Create test data
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        
        List<ProductForm> forms = Arrays.asList(
            TestData.productForm(uniqueBarcode1, "Product 1", client.getName(), 99.99),
            TestData.productForm(uniqueBarcode2, "Product 2", client.getName(), 88.88)
        );
        
        // Act - Create products through DTO bulk upload
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Successfully processed 2 rows", result.getMessage());
        assertEquals(2, result.getSuccessRows());
        
        // Assert - Verify database state using DAO select methods
        ProductPojo dbProduct1 = productDao.getByBarcode(uniqueBarcode1);
        ProductPojo dbProduct2 = productDao.getByBarcode(uniqueBarcode2);
        assertNotNull(dbProduct1);
        assertNotNull(dbProduct2);
        assertEquals("Product 1", dbProduct1.getName());
        assertEquals("Product 2", dbProduct2.getName());
    }

    @Test
    public void testBulkUploadProductsWithValidationErrors() throws ApiException {
        // Arrange - Create invalid forms
        List<ProductForm> forms = Arrays.asList(
            TestData.productForm("", "Product 1", "Client1", 99.99), // Invalid barcode
            TestData.productForm("BARCODE-2", "", "Client1", 88.88)  // Invalid name
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Validation failed"));
        assertEquals(2, result.getErrorRows());
        assertNotNull(result.getDownloadUrl());
    }

    @Test
    public void testBulkUploadProductsWithProcessingErrors() throws ApiException {
        // Arrange - Create test data
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        
        // Create first product
        ProductPojo existingProduct = TestData.productWithoutId(uniqueBarcode2, "Existing Product", client.getId());
        existingProduct.setMrp(77.77);
        productDao.insert(existingProduct);
        
        List<ProductForm> forms = Arrays.asList(
            TestData.productForm(uniqueBarcode1, "Product 1", client.getName(), 99.99),
            TestData.productForm(uniqueBarcode2, "Product 2", client.getName(), 88.88) // Duplicate barcode
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Processing failed"));
        assertEquals(1, result.getErrorRows());
        assertEquals(1, result.getSuccessRows());
        assertNotNull(result.getDownloadUrl());
        
        // Verify only the first product was created
        ProductPojo dbProduct1 = productDao.getByBarcode(uniqueBarcode1);
        assertNotNull(dbProduct1);
        assertEquals("Product 1", dbProduct1.getName());
    }

    @Test
    public void testBulkUploadProductsWithNegativeMrp() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode = getUniqueBarcode("BARCODE-1");
        List<ProductForm> forms = Arrays.asList(
            TestData.productForm(uniqueBarcode, "Product 1", client.getName(), -99.99) // Negative MRP
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Validation failed"));
        assertEquals(1, result.getErrorRows());
        assertNotNull(result.getDownloadUrl());
    }

    @Test
    public void testBulkUploadProductsWithNullValues() throws ApiException {
        // Arrange - Create forms with null values
        List<ProductForm> forms = Arrays.asList(
            TestData.productForm("BARCODE-1", null, "Client1", 99.99), // Null name
            TestData.productForm("BARCODE-2", "Product 2", null, 88.88) // Null client
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Validation failed"));
        assertEquals(2, result.getErrorRows());
        assertNotNull(result.getDownloadUrl());
    }

    @Test
    public void testBulkUploadProductsMaxRowsExceeded() throws ApiException {
        // Arrange - Create more than 5000 forms
        List<ProductForm> forms = new ArrayList<>();
        for (int i = 0; i < 5001; i++) {
            forms.add(TestData.productForm("BARCODE-" + i, "Product " + i, "Client1", 99.99));
        }
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Maximum 5000 rows allowed"));
        assertEquals(5001, result.getErrorRows());
    }

    @Test
    public void testBulkUploadProductsWithMixedValidAndInvalid() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        String uniqueBarcode1 = getUniqueBarcode("BARCODE-1");
        String uniqueBarcode2 = getUniqueBarcode("BARCODE-2");
        
        List<ProductForm> forms = Arrays.asList(
            TestData.productForm(uniqueBarcode1, "Product 1", client.getName(), 99.99), // Valid
            TestData.productForm("", "Product 2", client.getName(), 88.88), // Invalid barcode
            TestData.productForm(uniqueBarcode2, "", client.getName(), 77.77) // Invalid name
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(createMockMultipartFile(forms));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Validation failed"));
        assertEquals(2, result.getErrorRows());
        assertNotNull(result.getDownloadUrl());
    }

    @Test
    public void testBulkUploadProductsWithEmptyFieldsAndExtraColumns() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        // Create a TSV string with empty fields and extra columns
        String tsvContent = "barcode\tclientName\tname\tmrp\timageUrl\textraColumn\n" +
                "JUICE1\t" + client.getName() + "\tLime Juice\t50\t\textraValue\n" +  // Empty imageUrl
                "PIZ1\t" + client.getName() + "\tPizza\t100\thttps://example.com/image.jpg\textraValue\n" +  // Complete
                "COFFEE1\t" + client.getName() + "\tCappuccino\t299\t\textraValue";  // Empty imageUrl again
        
        MultipartFile file = new org.springframework.mock.web.MockMultipartFile(
            "file", "test.tsv", "text/tab-separated-values", tsvContent.getBytes()
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(file);
        
        // Assert - Should succeed despite empty imageUrl fields and extra columns
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Successfully processed 3 rows", result.getMessage());
        assertEquals(3, result.getSuccessRows());
        
        // Verify products were created correctly
        ProductPojo dbProduct1 = productDao.getByBarcode("JUICE1");
        ProductPojo dbProduct2 = productDao.getByBarcode("PIZ1");
        ProductPojo dbProduct3 = productDao.getByBarcode("COFFEE1");
        
        assertNotNull(dbProduct1);
        assertNotNull(dbProduct2);
        assertNotNull(dbProduct3);
        
        assertEquals("Lime Juice", dbProduct1.getName());
        assertEquals("Pizza", dbProduct2.getName());
        assertEquals("Cappuccino", dbProduct3.getName());
        
        assertEquals(50.0, dbProduct1.getMrp(), 0.01);
        assertEquals(100.0, dbProduct2.getMrp(), 0.01);
        assertEquals(299.0, dbProduct3.getMrp(), 0.01);
        
        // Check imageUrl handling - should be null for empty fields
        assertNull(dbProduct1.getImageUrl());
        assertEquals("https://example.com/image.jpg", dbProduct2.getImageUrl());
        assertNull(dbProduct3.getImageUrl());
    }

    @Test
    public void testBulkUploadProductsWithDifferentColumnOrder() throws ApiException {
        // Arrange - Create client using DAO
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientPojo client = TestData.clientWithoutId(uniqueClientName);
        clientDao.insert(client);
        client = clientDao.getClientByName(uniqueClientName);
        
        // Create a TSV string with different column order (name, mrp, barcode, clientName, imageUrl)
        String tsvContent = "name\tmrp\tbarcode\tclientName\timageUrl\n" +
                "Lime Juice\t50\tJUICE2\t" + client.getName() + "\thttps://example.com/juice.jpg\n" +
                "Pizza\t100\tPIZ2\t" + client.getName() + "\thttps://example.com/pizza.jpg\n" +
                "Cappuccino\t299\tCOFFEE2\t" + client.getName() + "\t";  // Empty imageUrl
        
        MultipartFile file = new org.springframework.mock.web.MockMultipartFile(
            "file", "test.tsv", "text/tab-separated-values", tsvContent.getBytes()
        );
        
        // Act
        TSVUploadResponse result = productDto.uploadProductMasterByTsv(file);
        
        // Assert - Should succeed with different column order
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Successfully processed 3 rows", result.getMessage());
        assertEquals(3, result.getSuccessRows());
        
        // Verify products were created correctly despite different column order
        ProductPojo dbProduct1 = productDao.getByBarcode("JUICE2");
        ProductPojo dbProduct2 = productDao.getByBarcode("PIZ2");
        ProductPojo dbProduct3 = productDao.getByBarcode("COFFEE2");
        
        assertNotNull(dbProduct1);
        assertNotNull(dbProduct2);
        assertNotNull(dbProduct3);
        
        assertEquals("Lime Juice", dbProduct1.getName());
        assertEquals("Pizza", dbProduct2.getName());
        assertEquals("Cappuccino", dbProduct3.getName());
        
        assertEquals(50.0, dbProduct1.getMrp(), 0.01);
        assertEquals(100.0, dbProduct2.getMrp(), 0.01);
        assertEquals(299.0, dbProduct3.getMrp(), 0.01);
        
        assertEquals("https://example.com/juice.jpg", dbProduct1.getImageUrl());
        assertEquals("https://example.com/pizza.jpg", dbProduct2.getImageUrl());
        assertNull(dbProduct3.getImageUrl());
    }
} 