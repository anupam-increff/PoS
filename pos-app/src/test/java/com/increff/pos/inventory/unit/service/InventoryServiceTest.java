package com.increff.pos.inventory.unit.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.InventoryService;
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
import static org.mockito.ArgumentMatchers.any;
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

    /**
     * Tests adding inventory to the system.
     * Verifies proper inventory insertion and database persistence.
     */
    @Test
    public void testAddInventory() {
        // Given
        doNothing().when(inventoryDao).insert(any(InventoryPojo.class));

        // When
        inventoryService.addInventory(1, 50);

        // Then
        verify(inventoryDao, times(1)).insert(any(InventoryPojo.class));
    }

    /**
     * Tests retrieving all inventory with pagination.
     * Verifies proper DAO delegation and pagination handling.
     */
    @Test
    public void testGetAll() {
        // Given
        List<InventoryPojo> inventoryList = Arrays.asList(testInventory);
        when(inventoryDao.getAllInventory(0, 10)).thenReturn(inventoryList);

        // When
        List<InventoryPojo> result = inventoryService.getAll(0, 10);

        // Then
        assertEquals("Result should match DAO response", inventoryList, result);
        verify(inventoryDao, times(1)).getAllInventory(0, 10);
    }

    /**
     * Tests counting all inventory items.
     * Verifies proper count delegation to DAO layer.
     */
    @Test
    public void testCountAll() {
        // Given
        when(inventoryDao.countAll()).thenReturn(5L);

        // When
        long result = inventoryService.countAll();

        // Then
        assertEquals("Count should match DAO response", 5L, result);
        verify(inventoryDao, times(1)).countAll();
    }

    /**
     * Tests searching inventory by barcode pattern.
     * Verifies proper search functionality with pagination.
     */
    @Test
    public void testSearchByBarcode() {
        // Given
        List<InventoryPojo> inventoryList = Arrays.asList(testInventory);
        when(inventoryDao.searchByBarcode("TEST", 0, 10)).thenReturn(inventoryList);

        // When
        List<InventoryPojo> result = inventoryService.searchByBarcode("TEST", 0, 10);

        // Then
        assertEquals("Search results should match DAO response", inventoryList, result);
        verify(inventoryDao, times(1)).searchByBarcode("TEST", 0, 10);
    }
} 