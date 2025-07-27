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

    @Test
    public void testUpdateInventory_Success() {
        // Given
        when(productService.getCheckProductByBarcode("INV-FLOW-001")).thenReturn(testProduct);
        doNothing().when(inventoryService).updateInventory(1, 75);

        // When
        inventoryFlow.updateInventory("INV-FLOW-001", 75);

        // Then
        verify(productService, times(1)).getCheckProductByBarcode("INV-FLOW-001");
        verify(inventoryService, times(1)).updateInventory(1, 75);
    }

    @Test
    public void testAddInventory_Success() {
        // Given
        when(productService.getCheckProductByBarcode("INV-FLOW-001")).thenReturn(testProduct);
        doNothing().when(inventoryService).addInventory(1, 25);

        // When
        inventoryFlow.addInventory("INV-FLOW-001", 25);

        // Then
        verify(productService, times(1)).getCheckProductByBarcode("INV-FLOW-001");
        verify(inventoryService, times(1)).addInventory(1, 25);
    }

    @Test
    public void testGetAll_Success() {
        // Given
        List<InventoryPojo> inventoryList = Arrays.asList(testInventory);
        when(inventoryService.getAll(0, 10)).thenReturn(inventoryList);
        when(inventoryService.countAll()).thenReturn(1L);
        when(productService.getCheckProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryFlow.getAll(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalItems());
        
        InventoryData data = result.getContent().get(0);
        assertEquals("INV-FLOW-001", data.getBarcode());
        assertEquals("Test Inventory Product", data.getName());
        assertEquals(Integer.valueOf(50), data.getQuantity());

        verify(inventoryService, times(1)).getAll(0, 10);
        verify(inventoryService, times(1)).countAll();
        verify(productService, times(1)).getCheckProductById(1);
    }

    @Test
    public void testSearchByBarcode_Success() {
        // Given
        List<InventoryPojo> inventoryList = Arrays.asList(testInventory);
        when(inventoryService.searchByBarcode("INV-FLOW", 0, 10)).thenReturn(inventoryList);
        when(inventoryService.countByBarcodeSearch("INV-FLOW")).thenReturn(1L);
        when(productService.getCheckProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryFlow.searchByBarcode("INV-FLOW", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalItems());
        
        InventoryData data = result.getContent().get(0);
        assertEquals("INV-FLOW-001", data.getBarcode());
        assertEquals("Test Inventory Product", data.getName());

        verify(inventoryService, times(1)).searchByBarcode("INV-FLOW", 0, 10);
        verify(inventoryService, times(1)).countByBarcodeSearch("INV-FLOW");
        verify(productService, times(1)).getCheckProductById(1);
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