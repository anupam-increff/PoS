package com.increff.pos.inventory.unit.flow;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryFlowTest {

    @InjectMocks
    private InventoryFlow flow;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ProductService productService;

    @Test
    public void testAddInventory() {
        ProductPojo product = new ProductPojo();
        product.setId(1);
        when(productService.getCheckProductByBarcode("TEST001")).thenReturn(product);

        flow.addInventory("TEST001", 100);

        verify(productService).getCheckProductByBarcode("TEST001");
        verify(inventoryService).addInventory(eq(1), eq(100));
    }

    @Test
    public void testUpdateInventory() {
        ProductPojo product = new ProductPojo();
        product.setId(1);
        when(productService.getCheckProductByBarcode("TEST001")).thenReturn(product);

        flow.updateInventory("TEST001", 200);

        verify(productService).getCheckProductByBarcode("TEST001");
        verify(inventoryService).updateInventory(eq(1), eq(200));
    }
} 