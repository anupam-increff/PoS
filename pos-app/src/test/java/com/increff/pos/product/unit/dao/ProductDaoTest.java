package com.increff.pos.product.unit.dao;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * DAO unit tests for ProductDao.
 * Tests direct database operations for Product entity.
 */
public class ProductDaoTest extends AbstractTest {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.clientWithoutId("Test Client Product DAO");
        clientDao.insert(testClient);
    }

    @Test
    public void testInsertAndGetByBarcode_Success() {
        // Given
        ProductPojo product = TestData.productWithoutId("DAO-001", "DAO Test Product", testClient.getId());

        // When
        productDao.insert(product);
        ProductPojo savedProduct = productDao.getByBarcode("DAO-001");

        // Then
        assertNotNull("Product should be saved and retrieved", savedProduct);
        assertEquals("DAO Test Product", savedProduct.getName());
        assertEquals("DAO-001", savedProduct.getBarcode());
        assertEquals(testClient.getId(), savedProduct.getClientId());
        assertNotNull("ID should be generated", savedProduct.getId());
    }

    @Test
    public void testGetByBarcode_NotFound() {
        // When
        ProductPojo product = productDao.getByBarcode("NONEXISTENT");

        // Then
        assertNull("Non-existent product should return null", product);
    }

    @Test
    public void testGetAllProducts_Success() {
        // Given
        productDao.insert(TestData.productWithoutId("PROD-1", "Product 1", testClient.getId()));
        productDao.insert(TestData.productWithoutId("PROD-2", "Product 2", testClient.getId()));

        // When
        List<ProductPojo> products = productDao.getAllProducts(0, 10);

        // Then
        assertNotNull("Products list should not be null", products);
        assertEquals(2, products.size());
    }

    @Test
    public void testSearchByBarcode_Success() {
        // Given
        productDao.insert(TestData.productWithoutId("SEARCH-001", "Search Product Alpha", testClient.getId()));
        productDao.insert(TestData.productWithoutId("SEARCH-002", "Search Product Beta", testClient.getId()));
        productDao.insert(TestData.productWithoutId("OTHER-001", "Different Product", testClient.getId()));

        // When
        List<ProductPojo> results = productDao.searchByBarcode("SEARCH", 0, 10);

        // Then
        assertNotNull("Search results should not be null", results);
        assertEquals(2, results.size());
        assertTrue("All results should contain 'SEARCH'",
            results.stream().allMatch(p -> p.getBarcode().contains("SEARCH")));
    }

    @Test
    public void testCountAll_Success() {
        // Given
        productDao.insert(TestData.productWithoutId("COUNT-1", "Count Product 1", testClient.getId()));
        productDao.insert(TestData.productWithoutId("COUNT-2", "Count Product 2", testClient.getId()));

        // When
        long count = productDao.countAll();

        // Then
        assertEquals(2L, count);
    }

    @Test
    public void testGetProductsByClientId_Success() {
        // Given
        ClientPojo anotherClient = TestData.clientWithoutId("Another Client");
        clientDao.insert(anotherClient);

        productDao.insert(TestData.productWithoutId("CLIENT-1", "Client Product 1", testClient.getId()));
        productDao.insert(TestData.productWithoutId("CLIENT-2", "Client Product 2", testClient.getId()));
        productDao.insert(TestData.productWithoutId("OTHER-1", "Other Product", anotherClient.getId()));

        // When
        List<ProductPojo> clientProducts = productDao.getProductsByClientId(testClient.getId(), 0, 10);

        // Then
        assertNotNull("Client products should not be null", clientProducts);
        assertEquals(2, clientProducts.size());
        assertTrue("All products should belong to test client",
            clientProducts.stream().allMatch(p -> p.getClientId().equals(testClient.getId())));
    }
} 