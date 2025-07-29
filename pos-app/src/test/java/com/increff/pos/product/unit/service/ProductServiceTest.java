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
    public void testAddProductWithUniqueBarcode() {
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(null);
        productService.addProduct(product);
        verify(productDao).insert(product);
    }

    @Test(expected = ApiException.class)
    public void testAddProductRejectsDuplicateBarcode() {
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(new ProductPojo());
        productService.addProduct(product);
    }

    @Test
    public void testValidateSellingPriceWithinMrp() {
        productService.validateSellingPrice(50.0, product);
    }

    @Test(expected = ApiException.class)
    public void testValidateSellingPriceRejectsValueAboveMrp() {
        productService.validateSellingPrice(100.0, product);
    }

    @Test
    public void testGetAllProductsWithPagination() {
        List<ProductPojo> products = Arrays.asList(product);
        when(productDao.getAllProducts(0, 10)).thenReturn(products);
        List<ProductPojo> result = productService.getAll(0, 10);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    public void testGetAllProductsReturnsEmptyList() {
        when(productDao.getAllProducts(0, 10)).thenReturn(Collections.emptyList());
        List<ProductPojo> result = productService.getAll(0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCountTotalProducts() {
        when(productDao.countAll()).thenReturn(5L);
        Long count = productService.countAll();
        assertEquals(Long.valueOf(5), count);
    }

    @Test
    public void testSearchProductsByBarcodeWithResults() {
        List<ProductPojo> products = Arrays.asList(product);
        when(productDao.searchByBarcode("BARCODE", 0, 10)).thenReturn(products);
        List<ProductPojo> result = productService.searchByBarcode("BARCODE", 0, 10);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    public void testCountProductsByBarcodeSearch() {
        when(productDao.countByBarcodeSearch("BARCODE")).thenReturn(2L);
        Long count = productService.countSearchByBarcode("BARCODE");
        assertEquals(Long.valueOf(2), count);
    }

    @Test
    public void testGetProductByValidBarcode() {
        when(productDao.getByBarcode("BARCODE-001")).thenReturn(product);
        ProductPojo result = productService.getCheckProductByBarcode("BARCODE-001");
        assertEquals("Test Product", result.getName());
    }

    @Test(expected = ApiException.class)
    public void testGetProductByNonExistentBarcode() {
        when(productDao.getByBarcode("NONEXISTENT")).thenReturn(null);
        productService.getCheckProductByBarcode("NONEXISTENT");
    }

    @Test
    public void testGetProductByValidId() {
        when(productDao.getById(1)).thenReturn(product);
        ProductPojo result = productService.getCheckProductById(1);
        assertEquals("Test Product", result.getName());
    }

    @Test(expected = ApiException.class)
    public void testGetProductByNonExistentId() {
        when(productDao.getById(999)).thenReturn(null);
        productService.getCheckProductById(999);
    }

    @Test
    public void testUpdateProductWithValidData() {
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
    public void testUpdateProductRejectsNonExistentId() {
        when(productDao.getById(999)).thenReturn(null);
        productService.update(999, new ProductPojo());
    }
} 