package com.increff.pos.unit.dto;

import com.increff.pos.config.TestData;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.form.InventoryForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void testAddInventory_FormValidation() {
        // Given
        doNothing().when(inventoryFlow).addInventory(anyString(), anyInt());

        // When
        inventoryDto.addInventory(testInventoryForm);

        // Then - Verify that form data is properly extracted and passed to flow
        verify(inventoryFlow, times(1)).addInventory(eq("BARCODE-001"), eq(50));
    }
} 