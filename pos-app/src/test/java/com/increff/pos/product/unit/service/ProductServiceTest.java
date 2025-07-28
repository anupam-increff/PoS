package com.increff.pos.product.unit.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    private ProductPojo testProduct;

    @Before
    public void setUp() {
        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("BARCODE-001");
        testProduct.setName("Test Product");
        testProduct.setMrp(99.99);
    }

    /**
     * Tests adding a new product to the system.
     * Verifies proper product insertion and duplicate checking.
     */
    @Test
    public void testAddProduct() {
        // Given
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(null);
        doNothing().when(productDao).insert(testProduct);

        // When
        productService.addProduct(testProduct);

        // Then
        verify(productDao, times(1)).getByBarcode("BARCODE-001");
        verify(productDao, times(1)).insert(testProduct);
    }

    @Test(expected = ApiException.class)
    public void testAddProduct_DuplicateBarcode() {
        // Given
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(testProduct);

        // When
        productService.addProduct(testProduct);

        // Then - exception should be thrown
    }

    /**
     * Tests retrieving a product by barcode with validation.
     * Verifies proper product lookup and error handling.
     */
    @Test
    public void testGetCheckProductByBarcode() {
        // Given
        when(productDao.getByBarcode("TEST-001")).thenReturn(testProduct);

        // When
        ProductPojo result = productService.getCheckProductByBarcode("TEST-001");

        // Then
        assertNotNull("Product should not be null", result);
        assertEquals("Product should match", testProduct, result);
        verify(productDao, times(1)).getByBarcode("TEST-001");
    }

    @Test(expected = ApiException.class)
    public void testGetCheckProductByBarcode_NotFound() {
        // Given
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(null);

        // When
        productService.getCheckProductByBarcode("BARCODE-001");

        // Then - exception should be thrown
    }

    /**
     * Tests retrieving a product by ID with validation.
     * Verifies proper product lookup by ID and error handling.
     */
    @Test
    public void testGetCheckProductById() {
        // Given
        when(productDao.getById(1)).thenReturn(testProduct);

        // When
        ProductPojo result = productService.getCheckProductById(1);

        // Then
        assertNotNull("Product should not be null", result);
        assertEquals("Product should match", testProduct, result);
        verify(productDao, times(1)).getById(1);
    }

    @Test(expected = ApiException.class)
    public void testGetCheckProductById_NotFound() {
        // Given
        when(productDao.getById(1)).thenReturn(null);

        // When
        productService.getCheckProductById(1);

        // Then - exception should be thrown
    }

    /**
     * Tests retrieving all products with pagination.
     * Verifies proper DAO delegation and pagination handling.
     */
    @Test
    public void testGetAll() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productDao.getAllProducts(0, 10)).thenReturn(products);

        // When
        List<ProductPojo> result = productService.getAll(0, 10);

        // Then
        assertEquals("Results should match DAO response", products, result);
        verify(productDao, times(1)).getAllProducts(0, 10);
    }

    /**
     * Tests counting all products in the system.
     * Verifies proper count delegation to DAO layer.
     */
    @Test
    public void testCountAll() {
        // Given
        when(productDao.countAll()).thenReturn(5L);

        // When
        long result = productService.countAll();

        // Then
        assertEquals("Count should match DAO response", 5L, result);
        verify(productDao, times(1)).countAll();
    }
} 