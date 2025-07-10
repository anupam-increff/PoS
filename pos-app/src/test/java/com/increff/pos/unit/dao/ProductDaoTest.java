package com.increff.pos.unit.dao;

import com.increff.pos.dao.ProductDao;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.increff.pos.setup.DaoTestConfig.class})
@Transactional
public class ProductDaoTest {

    @Autowired
    private ProductDao productDao;

    @Before
    public void setUp() {
        // Clean up any existing data
        // This will be handled by @Transactional rollback
    }

    @Test
    public void testGetByBarcode() {
        // Arrange
        ProductPojo product = TestData.productWithoutId("TEST-BARCODE-1", "TestProduct1", 1);
        productDao.insert(product);

        // Act
        ProductPojo result = productDao.getByBarcode("TEST-BARCODE-1");

        // Assert
        assertNotNull(result);
        assertEquals("TEST-BARCODE-1", result.getBarcode());
        assertEquals("TestProduct1", result.getName());
    }

    @Test
    public void testGetByBarcodeNotFound() {
        // Act
        ProductPojo result = productDao.getByBarcode("NONEXISTENT");

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetAllPaged() {
        // Arrange
        ProductPojo product1 = TestData.productWithoutId("TEST-BARCODE-PAGED1", "TestProductPaged1", 1);
        ProductPojo product2 = TestData.productWithoutId("TEST-BARCODE-PAGED2", "TestProductPaged2", 1);
        productDao.insert(product1);
        productDao.insert(product2);

        // Act
        List<ProductPojo> result = productDao.getAllPaged(0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    public void testCountAll() {
        // Arrange
        ProductPojo product1 = TestData.productWithoutId("TEST-BARCODE-COUNT1", "TestProductCount1", 1);
        ProductPojo product2 = TestData.productWithoutId("TEST-BARCODE-COUNT2", "TestProductCount2", 1);
        productDao.insert(product1);
        productDao.insert(product2);

        // Act
        long result = productDao.countAll();

        // Assert
        assertTrue(result >= 2);
    }

    @Test
    public void testGetByClientIdPaged() {
        // Arrange
        Integer clientId = 1;
        ProductPojo product1 = TestData.productWithoutId("TEST-BARCODE-CLIENT1", "TestProductClient1", clientId);
        ProductPojo product2 = TestData.productWithoutId("TEST-BARCODE-CLIENT2", "TestProductClient2", clientId);
        productDao.insert(product1);
        productDao.insert(product2);

        // Act
        List<ProductPojo> result = productDao.getByClientIdPaged(clientId, 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        for (ProductPojo product : result) {
            assertEquals(clientId, product.getClientId());
        }
    }

    @Test
    public void testCountByClientId() {
        // Arrange
        Integer clientId = 1;
        ProductPojo product1 = TestData.productWithoutId("TEST-BARCODE-COUNTCLIENT1", "TestProductCountClient1", clientId);
        ProductPojo product2 = TestData.productWithoutId("TEST-BARCODE-COUNTCLIENT2", "TestProductCountClient2", clientId);
        productDao.insert(product1);
        productDao.insert(product2);

        // Act
        long result = productDao.countByClientId(clientId);

        // Assert
        assertTrue(result >= 2);
    }

    @Test
    public void testSearchByBarcode() {
        // Arrange
        String searchTerm = "TEST-BARCODE-SEARCH";
        ProductPojo product1 = TestData.productWithoutId("TEST-BARCODE-SEARCH1", "TestProductSearch1", 1);
        ProductPojo product2 = TestData.productWithoutId("TEST-BARCODE-SEARCH2", "TestProductSearch2", 1);
        productDao.insert(product1);
        productDao.insert(product2);

        // Act
        List<ProductPojo> result = productDao.searchByBarcode(searchTerm, 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        for (ProductPojo product : result) {
            assertTrue(product.getBarcode().toLowerCase().contains(searchTerm.toLowerCase()));
        }
    }

    @Test
    public void testCountByBarcodeSearch() {
        // Arrange
        String searchTerm = "TEST-BARCODE-COUNTSEARCH";
        ProductPojo product1 = TestData.productWithoutId("TEST-BARCODE-COUNTSEARCH1", "TestProductCountSearch1", 1);
        ProductPojo product2 = TestData.productWithoutId("TEST-BARCODE-COUNTSEARCH2", "TestProductCountSearch2", 1);
        productDao.insert(product1);
        productDao.insert(product2);

        // Act
        long result = productDao.countByBarcodeSearch(searchTerm);

        // Assert
        assertTrue(result >= 2);
    }
} 