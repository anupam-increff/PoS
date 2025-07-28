package com.increff.pos.inventory.unit.dto;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryDtoTest {

    @Mock
    private InventoryFlow inventoryFlow;

    @InjectMocks
    private InventoryDto inventoryDto;

    private ProductPojo testProduct;
    private InventoryPojo testInventory;
    private InventoryForm testForm;

    @Before
    public void setUp() {
        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("TEST-001");
        testProduct.setName("Test Product");

        testInventory = TestData.inventoryWithoutId(1, 50);

        testForm = new InventoryForm();
        testForm.setBarcode("TEST-001");
        testForm.setQuantity(50);
    }

    /**
     * Tests adding inventory through the DTO layer.
     * Verifies proper form validation and flow integration.
     */
    @Test
    public void testAddInventory() {
        // Given
        doNothing().when(inventoryFlow).addInventory(anyString(), anyInt());

        // When
        inventoryDto.addInventory(testForm);

        // Then
        verify(inventoryFlow, times(1)).addInventory("TEST-001", 50);
    }

    /**
     * Tests updating inventory through the DTO layer.
     * Verifies proper form validation and flow integration.
     */
    @Test
    public void testUpdateInventory() {
        // Given
        doNothing().when(inventoryFlow).updateInventory(anyString(), anyInt());

        // When
        inventoryDto.updateInventoryByBarcode("TEST-001", testForm);

        // Then
        verify(inventoryFlow, times(1)).updateInventory("TEST-001", 50);
    }

    /**
     * Tests retrieving all inventory with pagination through DTO.
     * Verifies proper pagination and data conversion.
     */
    @Test
    public void testGetAll() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryFlow.getAll(0, 10)).thenReturn(inventories);
        when(inventoryFlow.countAll()).thenReturn(1L);
        when(inventoryFlow.getProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.getAll(0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one inventory", 1, result.getContent().size());
        assertEquals("Total items should match", 1L, result.getTotalItems());
        
        InventoryData data = result.getContent().get(0);
        assertEquals("Barcode should match", "TEST-001", data.getBarcode());
        assertEquals("Name should match", "Test Product", data.getName());
        assertEquals("Quantity should match", Integer.valueOf(50), data.getQuantity());
    }

    /**
     * Tests searching inventory by barcode through DTO.
     * Verifies proper search functionality and data conversion.
     */
    @Test
    public void testSearchByBarcode() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryFlow.searchByBarcode("TEST", 0, 10)).thenReturn(inventories);
        when(inventoryFlow.countByBarcodeSearch("TEST")).thenReturn(1L);
        when(inventoryFlow.getProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("TEST", 0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one inventory", 1, result.getContent().size());
        assertEquals("Total items should match", 1L, result.getTotalItems());
        
        InventoryData data = result.getContent().get(0);
        assertEquals("Barcode should match", "TEST-001", data.getBarcode());
        assertEquals("Name should match", "Test Product", data.getName());
        assertEquals("Quantity should match", Integer.valueOf(50), data.getQuantity());
    }

    /**
     * Tests case sensitivity in barcode search.
     * Verifies search works regardless of case.
     */
    @Test
    public void testSearchByCaseSensitivity() {
        // Given
        List<InventoryPojo> inventories = Arrays.asList(testInventory);
        when(inventoryFlow.searchByBarcode("test", 0, 10)).thenReturn(inventories);
        when(inventoryFlow.countByBarcodeSearch("test")).thenReturn(1L);
        when(inventoryFlow.getProductById(1)).thenReturn(testProduct);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("test", 0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one inventory", 1, result.getContent().size());
        assertEquals("Total items should match", 1L, result.getTotalItems());
    }

    /**
     * Tests searching with null barcode.
     * Verifies proper handling of null search term.
     */
    @Test
    public void testSearchByBarcodeNull() {
        // Given
        List<InventoryPojo> inventories = Collections.emptyList();
        when(inventoryFlow.searchByBarcode(null, 0, 10)).thenReturn(inventories);
        when(inventoryFlow.countByBarcodeSearch(null)).thenReturn(0L);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode(null, 0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should be empty", 0, result.getContent().size());
        assertEquals("Total items should be zero", 0L, result.getTotalItems());
    }
} 