package com.increff.pos.product.unit.dao;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ProductDaoTest extends AbstractTest {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;
    private ProductPojo testProduct;

    @Before
    public void setUp() {
        testClient = TestData.clientWithoutId("Test Client Product");
        clientDao.insert(testClient);

        testProduct = TestData.productWithoutId("PROD-001", "Test Product", testClient.getId());
    }

    /**
     * Tests inserting a product and retrieving it by barcode.
     * Verifies basic DAO insert and select operations.
     */
    @Test
    public void testInsertAndGetByBarcode() {
        // When
        productDao.insert(testProduct);

        // Then
        ProductPojo retrieved = productDao.getByBarcode("PROD-001");
        assertNotNull("Product should be found by barcode", retrieved);
        assertEquals("Product name should match", "Test Product", retrieved.getName());
        assertEquals("Client ID should match", testClient.getId(), retrieved.getClientId());
    }

    /**
     * Tests retrieving products by client ID.
     * Verifies filtering products by their associated client.
     */
    @Test
    public void testGetProductsByClientId() {
        // Given
        productDao.insert(testProduct);

        // When
        List<ProductPojo> products = productDao.getProductsByClientId(testClient.getId(), 0, 10);

        // Then
        assertNotNull("Product list should not be null", products);
        assertEquals("Should contain one product", 1, products.size());
        assertEquals("Product should match", testProduct.getName(), products.get(0).getName());
    }

    /**
     * Tests retrieving all products.
     * Verifies the getAll method returns all persisted products.
     */
    @Test
    public void testGetAllProducts() {
        // Given
        productDao.insert(testProduct);

        // When
        List<ProductPojo> allProducts = productDao.getAll();

        // Then
        assertNotNull("Product list should not be null", allProducts);
        assertTrue("Should contain at least one product", allProducts.size() >= 1);
    }

    /**
     * Tests retrieving product by non-existent barcode.
     * Verifies proper handling when no product matches the barcode.
     */
    @Test
    public void testGetByBarcodeNotFound() {
        // When
        ProductPojo result = productDao.getByBarcode("NON-EXISTENT");

        // Then
        assertNull("Non-existent product should return null", result);
    }

    /**
     * Tests searching products by barcode pattern.
     * Verifies partial matching and search functionality.
     */
    @Test
    public void testSearchByBarcode() {
        // Given
        productDao.insert(testProduct);

        // When
        List<ProductPojo> searchResults = productDao.searchByBarcode("PROD", 0, 10);

        // Then
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("Should find one matching product", 1, searchResults.size());
        assertEquals("Found product should match", "PROD-001", searchResults.get(0).getBarcode());
    }

    /**
     * Tests counting all products in the database.
     * Verifies the countAll method returns accurate count.
     */
    @Test
    public void testCountAll() {
        // Given
        long initialCount = productDao.countAll();
        productDao.insert(testProduct);

        // When
        long finalCount = productDao.countAll();

        // Then
        assertEquals("Count should increase by one", initialCount + 1, finalCount);
    }
} 