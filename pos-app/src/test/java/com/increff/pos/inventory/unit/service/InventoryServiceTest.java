package com.increff.pos.inventory.unit.service;

import com.increff.pos.setup.TestData;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.InventoryService;
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
public class InventoryServiceTest {

    @Mock
    private InventoryDao inventoryDao;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryPojo testInventory;

    @Before
    public void setUp() {
        testInventory = TestData.inventory(1, 1);
        testInventory.setQuantity(100);
    }

    @Test
    public void testAddInventory_Success() {
        // Given
        when(inventoryDao.getByProductId(1)).thenReturn(null);
        doNothing().when(inventoryDao).insert(any(InventoryPojo.class));

        // When
        inventoryService.addInventory(1, 50);

        // Then
        verify(inventoryDao, times(1)).getByProductId(1);
        verify(inventoryDao, times(1)).insert(any(InventoryPojo.class));
    }

    @Test
    public void testAddInventory_ExistingProduct() {
        // Given
        when(inventoryDao.getByProductId(1)).thenReturn(testInventory);

        // When
        inventoryService.addInventory(1, 50);

        // Then
        verify(inventoryDao, times(2)).getByProductId(1); // Called twice in the method
        assertEquals(Integer.valueOf(150), testInventory.getQuantity());
    }

    @Test
    public void testGetAll_Success() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryDao.getAllInventory(0, 10)).thenReturn(inventories);

        // When
        List<InventoryPojo> result = inventoryService.getAll(0, 10);

        // Then
        assertEquals(inventories, result);
        verify(inventoryDao, times(1)).getAllInventory(0, 10);
    }

    @Test
    public void testCountAll_Success() {
        // Given
        when(inventoryDao.countAll()).thenReturn(5L);

        // When
        long count = inventoryService.countAll();

        // Then
        assertEquals(5L, count);
        verify(inventoryDao, times(1)).countAll();
    }

    @Test
    public void testSearchByBarcode_Success() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryDao.searchByBarcode("TEST", 0, 10)).thenReturn(inventories);

        // When
        List<InventoryPojo> result = inventoryService.searchByBarcode("TEST", 0, 10);

        // Then
        assertEquals(inventories, result);
        verify(inventoryDao, times(1)).searchByBarcode("TEST", 0, 10);
    }
} 