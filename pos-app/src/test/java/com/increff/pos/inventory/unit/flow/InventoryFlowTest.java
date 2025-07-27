package com.increff.pos.inventory.unit.flow;

import com.increff.pos.setup.TestData;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
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
public class InventoryFlowTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private InventoryFlow inventoryFlow;

    private ProductPojo testProduct;
    private InventoryPojo testInventory;

    @Before
    public void setUp() {
        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("INV-FLOW-001");
        testProduct.setName("Test Inventory Product");

        testInventory = TestData.inventoryWithoutId(1, 50);
        testInventory.setId(1);
    }

    /**
     * Tests updating inventory quantity through flow layer.
     * Verifies proper business logic flow for inventory updates.
     */
    @Test
    public void testUpdateInventory() {
        // Given
        when(productService.getCheckProductByBarcode("TEST-001")).thenReturn(testProduct);
        doNothing().when(inventoryService).updateInventory(1, 50);

        // When
        inventoryFlow.updateInventory("TEST-001", 50);

        // Then
        verify(productService, times(1)).getCheckProductByBarcode("TEST-001");
        verify(inventoryService, times(1)).updateInventory(1, 50);
    }

    /**
     * Tests adding new inventory through flow layer.
     * Verifies proper inventory creation business logic.
     */
    @Test
    public void testAddInventory() {
        // Given
        when(productService.getCheckProductByBarcode("TEST-001")).thenReturn(testProduct);
        doNothing().when(inventoryService).addInventory(testProduct.getId(), 30);

        // When
        inventoryFlow.addInventory("TEST-001", 30);

        // Then
        verify(productService, times(1)).getCheckProductByBarcode("TEST-001");
        verify(inventoryService, times(1)).addInventory(1, 30);
    }

    /**
     * Tests retrieving all inventory with pagination.
     * Verifies proper delegation to service layer.
     */
    @Test
    public void testGetAll() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryService.getAll(0, 10)).thenReturn(inventories);
        when(inventoryService.countAll()).thenReturn(1L);
        when(productService.getCheckProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryFlow.getAll(0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one inventory", 1, result.getContent().size());
        verify(inventoryService, times(1)).getAll(0, 10);
        verify(inventoryService, times(1)).countAll();
    }

    /**
     * Tests searching inventory by barcode pattern.
     * Verifies proper search functionality through flow layer.
     */
    @Test
    public void testSearchByBarcode() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryService.searchByBarcode("TEST", 0, 10)).thenReturn(inventories);
        when(inventoryService.countByBarcodeSearch("TEST")).thenReturn(1L);
        when(productService.getCheckProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryFlow.searchByBarcode("TEST", 0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain search results", 1, result.getContent().size());
        verify(inventoryService, times(1)).searchByBarcode("TEST", 0, 10);
        verify(inventoryService, times(1)).countByBarcodeSearch("TEST");
    }

    @Test
    public void testGetAll_EmptyResult() {
        // Given
        when(inventoryService.getAll(0, 10)).thenReturn(Arrays.asList());
        when(inventoryService.countAll()).thenReturn(0L);

        // When
        PaginatedResponse<InventoryData> result = inventoryFlow.getAll(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0L, result.getTotalItems());

        verify(inventoryService, times(1)).getAll(0, 10);
        verify(inventoryService, times(1)).countAll();
    }
} 