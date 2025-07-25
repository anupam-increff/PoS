package com.increff.pos.unit.service;

import com.increff.pos.config.TestData;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Test
    public void testAddProduct_Success() {
        // Given
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(null);
        doNothing().when(productDao).insert(any(ProductPojo.class));

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

    @Test
    public void testGetCheckProductByBarcode_Success() {
        // Given
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(testProduct);

        // When
        ProductPojo result = productService.getCheckProductByBarcode("BARCODE-001");

        // Then
        assertEquals(testProduct, result);
        verify(productDao, times(1)).getByBarcode("BARCODE-001");
    }

    @Test(expected = ApiException.class)
    public void testGetCheckProductByBarcode_NotFound() {
        // Given
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(null);

        // When
        productService.getCheckProductByBarcode("BARCODE-001");

        // Then - exception should be thrown
    }

    @Test
    public void testGetCheckProductById_Success() {
        // Given
        when(productDao.getById(1)).thenReturn(testProduct);

        // When
        ProductPojo result = productService.getCheckProductById(1);

        // Then
        assertEquals(testProduct, result);
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

    @Test
    public void testGetAll_Success() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productDao.getAllProducts(0, 10)).thenReturn(products);

        // When
        List<ProductPojo> result = productService.getAll(0, 10);

        // Then
        assertEquals(products, result);
        verify(productDao, times(1)).getAllProducts(0, 10);
    }

    @Test
    public void testCountAll_Success() {
        // Given
        when(productDao.countAll()).thenReturn(5L);

        // When
        long count = productService.countAll();

        // Then
        assertEquals(5L, count);
        verify(productDao, times(1)).countAll();
    }
} 