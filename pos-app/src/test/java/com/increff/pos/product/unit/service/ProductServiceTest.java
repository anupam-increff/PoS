package com.increff.pos.product.unit.service;

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
import java.util.Collections;
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

    private ProductPojo product;

    @Before
    public void setUp() {
        product = new ProductPojo();
        product.setId(1);
        product.setBarcode("BARCODE-001");
        product.setName("Test Product");
        product.setMrp(99.99);
        product.setClientId(1);
    }

    @Test
    public void addProduct() {
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(null);
        productService.addProduct(product);
        verify(productDao).insert(product);
    }

    @Test(expected = ApiException.class)
    public void addProductDuplicateBarcode() {
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(new ProductPojo());
        productService.addProduct(product);
    }

    @Test
    public void validateSellingPrice() {
        productService.validateSellingPrice(50.0, product);
    }

    @Test(expected = ApiException.class)
    public void validateSellingPriceExceedsMrp() {
        productService.validateSellingPrice(100.0, product);
    }

    @Test
    public void getAll() {
        List<ProductPojo> products = Arrays.asList(product);
        when(productDao.getAllProducts(0, 10)).thenReturn(products);
        List<ProductPojo> result = productService.getAll(0, 10);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    public void getAllEmpty() {
        when(productDao.getAllProducts(0, 10)).thenReturn(Collections.emptyList());
        List<ProductPojo> result = productService.getAll(0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    public void countAll() {
        when(productDao.countAll()).thenReturn(5L);
        Long count = productService.countAll();
        assertEquals(Long.valueOf(5), count);
    }

    @Test
    public void searchByBarcode() {
        List<ProductPojo> products = Arrays.asList(product);
        when(productDao.searchByBarcode("BARCODE", 0, 10)).thenReturn(products);
        List<ProductPojo> result = productService.searchByBarcode("BARCODE", 0, 10);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    public void countSearchByBarcode() {
        when(productDao.countByBarcodeSearch("BARCODE")).thenReturn(2L);
        Long count = productService.countSearchByBarcode("BARCODE");
        assertEquals(Long.valueOf(2), count);
    }

    @Test
    public void getCheckProductByBarcode() {
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(product);
        ProductPojo result = productService.getCheckProductByBarcode("BARCODE-001");
        assertEquals("Test Product", result.getName());
    }

    @Test(expected = ApiException.class)
    public void getCheckProductByBarcodeNotFound() {
        when(productDao.getByBarcode("NONEXISTENT")).thenReturn(null);
        productService.getCheckProductByBarcode("NONEXISTENT");
    }

    @Test
    public void getCheckProductById() {
        when(productDao.getById(1)).thenReturn(product);
        ProductPojo result = productService.getCheckProductById(1);
        assertEquals("Test Product", result.getName());
    }

    @Test(expected = ApiException.class)
    public void getCheckProductByIdNotFound() {
        when(productDao.getById(999)).thenReturn(null);
        productService.getCheckProductById(999);
    }

    @Test
    public void update() {
        when(productDao.getById(1)).thenReturn(product);
        ProductPojo updated = new ProductPojo();
        updated.setName("Updated Product");
        updated.setMrp(149.99);
        updated.setClientId(2);
        productService.update(1, updated);
        assertEquals("Updated Product", product.getName());
        assertEquals(149.99, product.getMrp(), 0.01);
        assertEquals(Integer.valueOf(2), product.getClientId());
    }

    @Test(expected = ApiException.class)
    public void updateNonExistent() {
        when(productDao.getById(999)).thenReturn(null);
        productService.update(999, new ProductPojo());
    }
} 