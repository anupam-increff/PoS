package com.increff.pos.product.integration.dao;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ProductDaoTest extends AbstractTest {

    @Autowired
    private ProductDao productDao;

    private ProductPojo product;

    @Before
    public void setUp() {
        product = new ProductPojo();
        product.setBarcode("BARCODE-001");
        product.setName("Test Product");
        product.setMrp(99.99);
        product.setClientId(1);
    }

    @Test
    public void insertAndGetById() {
        productDao.insert(product);
        ProductPojo fetched = productDao.getById(product.getId());
        assertNotNull(fetched);
        assertEquals("BARCODE-001", fetched.getBarcode());
        assertEquals("Test Product", fetched.getName());
        assertEquals(99.99, fetched.getMrp(), 0.01);
    }

    @Test
    public void getByBarcode() {
        productDao.insert(product);
        ProductPojo fetched = productDao.getByBarcode("BARCODE-001");
        assertNotNull(fetched);
        assertEquals("Test Product", fetched.getName());
    }

    @Test
    public void getAllProducts() {
        productDao.insert(product);

        ProductPojo product2 = new ProductPojo();
        product2.setBarcode("BARCODE-002");
        product2.setName("Another Product");
        product2.setMrp(149.99);
        product2.setClientId(1);
        productDao.insert(product2);

        List<ProductPojo> products = productDao.getAllProducts(0, 10);
        assertEquals(2, products.size());
    }

    @Test
    public void countAll() {
        productDao.insert(product);

        ProductPojo product2 = new ProductPojo();
        product2.setBarcode("BARCODE-002");
        product2.setName("Another Product");
        product2.setMrp(149.99);
        product2.setClientId(1);
        productDao.insert(product2);

        Long count = productDao.countAll();
        assertEquals(Long.valueOf(2), count);
    }

    @Test
    public void searchByBarcode() {
        productDao.insert(product);

        List<ProductPojo> results = productDao.searchByBarcode("BARCODE", 0, 10);
        assertEquals(1, results.size());
        assertEquals("BARCODE-001", results.get(0).getBarcode());
    }

    @Test
    public void countByBarcodeSearch() {
        productDao.insert(product);

        Long count = productDao.countByBarcodeSearch("BARCODE");
        assertEquals(Long.valueOf(1), count);
    }

    @Test
    public void getProductsByClientId() {
        productDao.insert(product);

        ProductPojo product2 = new ProductPojo();
        product2.setBarcode("BARCODE-002");
        product2.setName("Another Product");
        product2.setMrp(149.99);
        product2.setClientId(2);
        productDao.insert(product2);

        List<ProductPojo> products = productDao.getProductsByClientId(1, 0, 10);
        assertEquals(1, products.size());
        assertEquals("BARCODE-001", products.get(0).getBarcode());
    }

    @Test
    public void countByClientId() {
        productDao.insert(product);

        ProductPojo product2 = new ProductPojo();
        product2.setBarcode("BARCODE-002");
        product2.setName("Another Product");
        product2.setMrp(149.99);
        product2.setClientId(2);
        productDao.insert(product2);

        Long count = productDao.countByClientId(1);
        assertEquals(Long.valueOf(1), count);
    }
} 