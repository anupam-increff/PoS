package com.increff.pos.inventory.integration.dao;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class InventoryDaoIntegrationTest extends AbstractTest {

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProductDao productDao;

    private Integer productId;

    @Before
    public void setUp() {
        createTestProduct();
    }

    @Test
    public void testInsert() {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(100);
        inventoryDao.insert(pojo);

        InventoryPojo fetched = inventoryDao.getByProductId(productId);
        assertNotNull(fetched);
        assertEquals(Integer.valueOf(100), fetched.getQuantity());
    }

    @Test
    public void testGetByProductId() {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(100);
        inventoryDao.insert(pojo);

        InventoryPojo fetched = inventoryDao.getByProductId(productId);
        assertNotNull(fetched);
        assertEquals(productId, fetched.getProductId());
        assertEquals(Integer.valueOf(100), fetched.getQuantity());
    }

    @Test
    public void testGetByProductIdNotFound() {
        InventoryPojo fetched = inventoryDao.getByProductId(999);
        assertNull(fetched);
    }

    @Test
    public void testGetAllInventory() {
        createInventory(productId, 100);
        ProductPojo product2 = createProduct("TEST002");
        createInventory(product2.getId(), 200);

        List<InventoryPojo> list = inventoryDao.getAllInventory(0, 10);
        assertEquals(2, list.size());
    }

    @Test
    public void testGetAllInventoryPagination() {
        createInventory(productId, 100);
        ProductPojo product2 = createProduct("TEST002");
        createInventory(product2.getId(), 200);
        ProductPojo product3 = createProduct("TEST003");
        createInventory(product3.getId(), 300);

        List<InventoryPojo> page1 = inventoryDao.getAllInventory(0, 2);
        assertEquals(2, page1.size());

        List<InventoryPojo> page2 = inventoryDao.getAllInventory(1, 2);
        assertEquals(1, page2.size());
    }

    @Test
    public void testCountAll() {
        createInventory(productId, 100);
        ProductPojo product2 = createProduct("TEST002");
        createInventory(product2.getId(), 200);

        Long count = inventoryDao.countAll();
        assertEquals(2L, count.longValue());
    }

    @Test
    public void testSearchByBarcode() {
        createInventory(productId, 100);
        ProductPojo product2 = createProduct("TEST002");
        createInventory(product2.getId(), 200);

        List<InventoryPojo> list = inventoryDao.searchByBarcode("TEST001", 0, 10);
        assertEquals(1, list.size());
        assertEquals(productId, list.get(0).getProductId());
    }

    @Test
    public void testCountByBarcodeSearch() {
        createInventory(productId, 100);
        ProductPojo product2 = createProduct("TEST002");
        createInventory(product2.getId(), 200);

        Long count = inventoryDao.countByBarcodeSearch("TEST001");
        assertEquals(1L, count.longValue());
    }

    private void createTestProduct() {
        ProductPojo product = new ProductPojo();
        product.setBarcode("TEST001");
        product.setName("Test Product");
        product.setMrp(100.0);
        product.setClientId(1);
        productDao.insert(product);
        productId = product.getId();
    }

    private ProductPojo createProduct(String barcode) {
        ProductPojo product = new ProductPojo();
        product.setBarcode(barcode);
        product.setName("Test Product");
        product.setMrp(100.0);
        product.setClientId(1);
        productDao.insert(product);
        return product;
    }

    private void createInventory(Integer productId, Integer quantity) {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(quantity);
        inventoryDao.insert(pojo);
    }
} 