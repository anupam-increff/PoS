package com.increff.pos.integration.product;

import com.increff.pos.config.IntegrationTestConfig;
import com.increff.pos.config.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
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
 * Integration tests for basic Product CRUD operations.
 * Tests product creation, retrieval, and validation scenarios.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestConfig.class})
@Transactional
public class ProductOperationsIntegrationTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        // Create test client for product operations
        testClient = TestData.clientWithoutId("TestClient");
        clientDao.insert(testClient);
    }

    @Test
    public void testAddProduct_Success() throws ApiException {
        // Given: Valid product form
        ProductForm form = TestData.productForm("BARCODE-001", "Test Product", "TestClient", 100.0);

        // When: Adding product
        productDto.addProduct(form);

        // Then: Product should be saved in database
        ProductPojo savedProduct = productDao.getByBarcode("BARCODE-001");
        assertNotNull("Product should be saved", savedProduct);
        assertEquals("BARCODE-001", savedProduct.getBarcode());
        assertEquals("Test Product", savedProduct.getName());
        assertEquals(testClient.getId(), savedProduct.getClientId());
    }

    @Test(expected = ApiException.class)
    public void testAddProduct_NonExistentClient() {
        // Given: Product form with non-existent client
        ProductForm form = TestData.productForm("BARCODE-002", "Test Product", "NonExistentClient", 100.0);

        // When: Adding product with invalid client
        // Then: Should throw ApiException
        productDto.addProduct(form);
    }

    @Test(expected = ApiException.class)
    public void testAddProduct_DuplicateBarcode() throws ApiException {
        // Given: Two products with same barcode
        ProductForm form1 = TestData.productForm("BARCODE-003", "Product 1", "TestClient", 100.0);
        ProductForm form2 = TestData.productForm("BARCODE-003", "Product 2", "TestClient", 200.0);

        // When: Adding first product (should succeed)
        productDto.addProduct(form1);

        // Then: Adding second product with same barcode should fail
        productDto.addProduct(form2);
    }

    @Test
    public void testGetProductByBarcode_Success() throws ApiException {
        // Given: Product exists in database
        ProductForm form = TestData.productForm("BARCODE-004", "Test Product", "TestClient", 150.0);
        productDto.addProduct(form);

        // When: Retrieving product by barcode
        ProductData result = productDto.getByBarcode("BARCODE-004");

        // Then: Should return correct product data
        assertNotNull("Product should be found", result);
        assertEquals("BARCODE-004", result.getBarcode());
        assertEquals("Test Product", result.getName());
        assertEquals("TestClient", result.getClientName());
    }

    @Test(expected = ApiException.class)
    public void testGetProductByBarcode_NotFound() {
        // Given: Product doesn't exist
        // When: Retrieving non-existent product
        // Then: Should throw ApiException
        productDto.getByBarcode("NON-EXISTENT");
    }

    @Test
    public void testGetAllProducts() throws ApiException {
        // Given: Multiple products exist
        productDto.addProduct(TestData.productForm("BARCODE-005", "Product 1", "TestClient", 100.0));
        productDto.addProduct(TestData.productForm("BARCODE-006", "Product 2", "TestClient", 200.0));

        // When: Getting all products
        PaginatedResponse<ProductData> response = productDto.getAll(0, 10);

        // Then: Should return all products with pagination info
        assertNotNull("Response should not be null", response);
        assertTrue("Should have products", response.getContent().size() >= 2);
        assertTrue("Total count should be at least 2", response.getTotalItems() >= 2);
    }
} 