package com.increff.pos.inventory.unit.dto;

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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryDtoTest {

    @Mock
    private InventoryFlow inventoryFlow;

    @InjectMocks
    private InventoryDto inventoryDto;

    private Validator validator;
    private InventoryForm testInventoryForm;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        testInventoryForm = new InventoryForm();
        testInventoryForm.setBarcode("INV-001");
        testInventoryForm.setQuantity(50);
    }

    /**
     * Tests adding inventory with valid data.
     * Verifies proper flow delegation and processing.
     */
    @Test
    public void testAddInventory() {
        // Given
        doNothing().when(inventoryFlow).addInventory(anyString(), anyInt());

        // When
        inventoryDto.addInventory(testInventoryForm);

        // Then
        verify(inventoryFlow, times(1)).addInventory("INV-001", 50);
    }

    /**
     * Tests adding inventory with null barcode.
     * Verifies proper validation handling.
     */
    @Test
    public void testAddInventoryNullBarcode() {
        // Given
        testInventoryForm.setBarcode(null);

        // When
        Set<ConstraintViolation<InventoryForm>> violations = validator.validate(testInventoryForm);

        // Then
        assertFalse("Should have validation violations for null barcode", violations.isEmpty());
    }

    /**
     * Tests adding inventory with empty barcode.
     * Verifies proper validation for empty strings.
     */
    @Test
    public void testAddInventoryEmptyBarcode() {
        // Given
        testInventoryForm.setBarcode("");

        // When
        Set<ConstraintViolation<InventoryForm>> violations = validator.validate(testInventoryForm);

        // Then
        assertFalse("Should have validation violations for empty barcode", violations.isEmpty());
    }

    /**
     * Tests adding inventory with barcode containing only spaces.
     * Verifies proper validation for whitespace-only input.
     */
    @Test
    public void testAddInventoryWhitespaceBarcode() {
        // Given
        testInventoryForm.setBarcode("   ");

        // When
        Set<ConstraintViolation<InventoryForm>> violations = validator.validate(testInventoryForm);

        // Then
        assertFalse("Should have validation violations for whitespace-only barcode", violations.isEmpty());
    }

    /**
     * Tests adding inventory with negative quantity.
     * Verifies proper validation for invalid quantities.
     */
    @Test
    public void testAddInventoryNegativeQuantity() {
        // Given
        testInventoryForm.setQuantity(-10);

        // When
        Set<ConstraintViolation<InventoryForm>> violations = validator.validate(testInventoryForm);

        // Then
        assertFalse("Should have validation violations for negative quantity", violations.isEmpty());
    }

    /**
     * Tests adding inventory with zero quantity.
     * Verifies proper validation for zero values.
     */
    @Test
    public void testAddInventoryZeroQuantity() {
        // Given
        testInventoryForm.setQuantity(0);

        // When
        Set<ConstraintViolation<InventoryForm>> violations = validator.validate(testInventoryForm);

        // Then
        assertFalse("Should have validation violations for zero quantity", violations.isEmpty());
    }

    /**
     * Tests updating inventory by barcode with valid data.
     * Verifies proper flow delegation for updates.
     */
    @Test
    public void testUpdateInventoryByBarcode() {
        // Given
        doNothing().when(inventoryFlow).updateInventory(anyString(), anyInt());

        // When
        inventoryDto.updateInventoryByBarcode("INV-001", testInventoryForm);

        // Then
        verify(inventoryFlow, times(1)).updateInventory("INV-001", 50);
    }

    /**
     * Tests retrieving all inventory with pagination.
     * Verifies proper flow delegation and response handling.
     */
    @Test
    public void testGetAll() {
        // Given
        PaginatedResponse<InventoryData> mockResponse = new PaginatedResponse<>();
        when(inventoryFlow.getAll(0, 10)).thenReturn(mockResponse);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.getAll(0, 10);

        // Then
        assertEquals("Should return flow response", mockResponse, result);
        verify(inventoryFlow, times(1)).getAll(0, 10);
    }

    /**
     * Tests searching inventory by barcode with pagination.
     * Verifies proper search functionality delegation.
     */
    @Test
    public void testSearchByBarcode() {
        // Given
        PaginatedResponse<InventoryData> mockResponse = new PaginatedResponse<>();
        when(inventoryFlow.searchByBarcode("INV", 0, 10)).thenReturn(mockResponse);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("INV", 0, 10);

        // Then
        assertEquals("Should return search results", mockResponse, result);
        verify(inventoryFlow, times(1)).searchByBarcode("INV", 0, 10);
    }

    /**
     * Tests searching with case sensitivity.
     * Verifies case handling in search functionality.
     */
    @Test
    public void testSearchByCaseSensitivity() {
        // Given
        PaginatedResponse<InventoryData> mockResponse = new PaginatedResponse<>();
        when(inventoryFlow.searchByBarcode("inv", 0, 10)).thenReturn(mockResponse);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("inv", 0, 10);

        // Then
        assertEquals("Should handle case properly", mockResponse, result);
        verify(inventoryFlow, times(1)).searchByBarcode("inv", 0, 10);
    }

    /**
     * Tests searching with null search term.
     * Verifies proper handling of null search parameters.
     */
    @Test
    public void testSearchByBarcodeNull() {
        // Given
        PaginatedResponse<InventoryData> mockResponse = new PaginatedResponse<>();
        when(inventoryFlow.searchByBarcode(null, 0, 10)).thenReturn(mockResponse);

        // When
        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode(null, 0, 10);

        // Then
        assertEquals("Should handle null search term", mockResponse, result);
        verify(inventoryFlow, times(1)).searchByBarcode(null, 0, 10);
    }
} 