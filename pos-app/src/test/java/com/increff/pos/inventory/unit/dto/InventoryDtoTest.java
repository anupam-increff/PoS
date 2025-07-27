package com.increff.pos.inventory.unit.dto;

import com.increff.pos.setup.TestData;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryDtoTest {

    @Mock
    private InventoryFlow inventoryFlow;

    @InjectMocks
    private InventoryDto inventoryDto;

    private InventoryForm testInventoryForm;

    @Before
    public void setUp() {
        testInventoryForm = TestData.inventoryForm("BARCODE-001", 50);
    }

    @Test
    public void testAddInventory_Success() {
        // Given
        doNothing().when(inventoryFlow).addInventory(anyString(), anyInt());

        // When
        inventoryDto.addInventory(testInventoryForm);

        // Then
        verify(inventoryFlow, times(1)).addInventory("BARCODE-001", 50);
    }

    @Test
    public void testGetAll_Success() {
        // Given
        PaginatedResponse<InventoryData> mockResponse = new PaginatedResponse<>();
        when(inventoryFlow.getAll(0, 10)).thenReturn(mockResponse);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.getAll(0, 10);

        // Then
        assertEquals(mockResponse, result);
        verify(inventoryFlow, times(1)).getAll(0, 10);
    }

    @Test
    public void testSearchByBarcode_Success() {
        // Given
        PaginatedResponse<InventoryData> mockResponse = new PaginatedResponse<>();
        when(inventoryFlow.searchByBarcode("BAR", 0, 10)).thenReturn(mockResponse);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("BAR", 0, 10);

        // Then
        assertEquals(mockResponse, result);
        verify(inventoryFlow, times(1)).searchByBarcode("BAR", 0, 10);
    }

    @Test
    public void testUpdateInventoryByBarcode_Success() {
        // Given
        InventoryForm updateForm = TestData.inventoryForm("BARCODE-001", 75);
        doNothing().when(inventoryFlow).updateInventory(anyString(), anyInt());

        // When
        inventoryDto.updateInventoryByBarcode("BARCODE-001", updateForm);

        // Then
        verify(inventoryFlow, times(1)).updateInventory("BARCODE-001", 75);
    }
} 