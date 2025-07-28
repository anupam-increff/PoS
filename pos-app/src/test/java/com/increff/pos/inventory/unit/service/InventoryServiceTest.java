package com.increff.pos.inventory.unit.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.InventoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceTest {

    @InjectMocks
    private InventoryService service;

    @Mock
    private InventoryDao dao;

    @Test
    public void testGetCheckByProductId() throws ApiException {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(1);
        pojo.setQuantity(10);

        when(dao.getByProductId(1)).thenReturn(pojo);

        InventoryPojo result = service.getCheckByProductId(1);
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getProductId());
        assertEquals(Integer.valueOf(10), result.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testGetCheckByProductIdNotFound() throws ApiException {
        when(dao.getByProductId(1)).thenReturn(null);
        service.getCheckByProductId(1);
    }

    @Test
    public void testGetAll() {
        InventoryPojo pojo1 = new InventoryPojo();
        pojo1.setProductId(1);
        pojo1.setQuantity(10);

        InventoryPojo pojo2 = new InventoryPojo();
        pojo2.setProductId(2);
        pojo2.setQuantity(20);

        when(dao.getAllInventory(0, 10)).thenReturn(Arrays.asList(pojo1, pojo2));

        List<InventoryPojo> result = service.getAll(0, 10);
        assertEquals(2, result.size());
    }

    @Test
    public void testAddInventory() throws ApiException {
        when(dao.getByProductId(1)).thenReturn(null);

        service.addInventory(1, 10);

        verify(dao).insert(any(InventoryPojo.class));
    }

    @Test(expected = ApiException.class)
    public void testAddInventoryNegativeQuantity() throws ApiException {
        service.addInventory(1, -10);
    }

    @Test(expected = ApiException.class)
    public void testAddInventoryNullQuantity() throws ApiException {
        service.addInventory(1, null);
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        InventoryPojo existingPojo = new InventoryPojo();
        existingPojo.setProductId(1);
        existingPojo.setQuantity(10);

        when(dao.getByProductId(1)).thenReturn(existingPojo);

        service.updateInventory(1, 20);

        assertEquals(Integer.valueOf(20), existingPojo.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testUpdateInventoryNotFound() throws ApiException {
        when(dao.getByProductId(1)).thenReturn(null);
        service.updateInventory(1, 20);
    }

    @Test(expected = ApiException.class)
    public void testUpdateInventoryNegativeQuantity() throws ApiException {
        lenient().when(dao.getByProductId(1)).thenReturn(new InventoryPojo());
        service.updateInventory(1, -1);
    }

    @Test(expected = ApiException.class)
    public void testUpdateInventoryNullQuantity() throws ApiException {
        lenient().when(dao.getByProductId(1)).thenReturn(new InventoryPojo());
        service.updateInventory(1, null);
    }

    @Test
    public void testValidateSufficientAndReduceInventory() throws ApiException {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setQuantity(100);

        when(dao.getByProductId(1)).thenReturn(inventory);

        service.validateSufficientAndReduceInventory(1, 50, "Test Product");

        assertEquals(Integer.valueOf(50), inventory.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testValidateSufficientAndReduceInventoryInsufficientQuantity() throws ApiException {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setQuantity(30);

        when(dao.getByProductId(1)).thenReturn(inventory);

        service.validateSufficientAndReduceInventory(1, 50, "Test Product");
    }
} 