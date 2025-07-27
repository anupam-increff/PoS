package com.increff.pos.unit.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.config.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryFlowTest {

    @InjectMocks
    private InventoryFlow inventoryFlow;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ProductService productService;

    @Mock
    private TSVDownloadService tsvDownloadService;

    private ProductPojo product1;
    private ProductPojo product2;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        product1 = TestData.product(1, 1);
        product2 = TestData.product(2, 1);
    }

    @Test
    public void testAddInventory() {
        // Arrange
        when(productService.getCheckProductByBarcode("BARCODE-1")).thenReturn(product1);
        doNothing().when(inventoryService).addInventory(product1.getId(), 100);
        
        // Act
        inventoryFlow.addInventory("BARCODE-1", 100);
        
        // Assert
        verify(inventoryService, times(1)).addInventory(product1.getId(), 100);
    }

    @Test
    public void testUpdateInventory() {
        // Arrange
        when(productService.getCheckProductByBarcode("BARCODE-1")).thenReturn(product1);
        doNothing().when(inventoryService).updateInventory(product1.getId(), 150);
        
        // Act
        inventoryFlow.updateInventory("BARCODE-1", 150);
        
        // Assert
        verify(inventoryService, times(1)).updateInventory(product1.getId(), 150);
    }
} 