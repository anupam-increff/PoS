package com.increff.pos.unit.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddProduct_Success() {
        ProductPojo product = createTestProduct("BARCODE-1", "Test Product", 100.0);
        when(productDao.getByBarcode("BARCODE-1")).thenReturn(null);

        productService.addProduct(product);

        verify(productDao).insert(product);
    }

    @Test(expected = ApiException.class)
    public void testAddProduct_DuplicateBarcode() {
        ProductPojo existing = createTestProduct("BARCODE-1", "Existing Product", 50.0);
        ProductPojo newProduct = createTestProduct("BARCODE-1", "New Product", 100.0);
        
        when(productDao.getByBarcode("BARCODE-1")).thenReturn(existing);

        productService.addProduct(newProduct);
    }

    @Test
    public void testGetCheckProductByBarcode_Success() {
        ProductPojo product = createTestProduct("BARCODE-1", "Test Product", 100.0);
        when(productDao.getByBarcode("BARCODE-1")).thenReturn(product);

        ProductPojo result = productService.getCheckProductByBarcode("BARCODE-1");

        assertEquals(product, result);
    }

    @Test(expected = ApiException.class)
    public void testGetCheckProductByBarcode_NotFound() {
        when(productDao.getByBarcode("BARCODE-1")).thenReturn(null);

        productService.getCheckProductByBarcode("BARCODE-1");
    }

    @Test
    public void testGetAll() {
        List<ProductPojo> products = Arrays.asList(
            createTestProduct("BARCODE-1", "Product 1", 100.0),
            createTestProduct("BARCODE-2", "Product 2", 200.0)
        );
        when(productDao.getAllProducts(0, 10)).thenReturn(products);

        List<ProductPojo> result = productService.getAll(0, 10);

        assertEquals(2, result.size());
        verify(productDao).getAllProducts(0, 10);
    }

    @Test
    public void testCountAll() {
        when(productDao.countAll()).thenReturn(5L);

        long count = productService.countAll();

        assertEquals(5L, count);
    }

    @Test
    public void testSearchByBarcode() {
        List<ProductPojo> products = Arrays.asList(
            createTestProduct("BARCODE-1", "Product 1", 100.0)
        );
        when(productDao.searchByBarcode("BARCODE", 0, 10)).thenReturn(products);

        List<ProductPojo> result = productService.searchByBarcode("BARCODE", 0, 10);

        assertEquals(1, result.size());
        verify(productDao).searchByBarcode("BARCODE", 0, 10);
    }

    private ProductPojo createTestProduct(String barcode, String name, Double mrp) {
        ProductPojo product = new ProductPojo();
        product.setBarcode(barcode);
        product.setName(name);
        product.setMrp(mrp);
        product.setClientId(1);
        return product;
    }
} 