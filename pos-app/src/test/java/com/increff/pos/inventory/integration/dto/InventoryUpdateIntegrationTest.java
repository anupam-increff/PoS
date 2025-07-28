package com.increff.pos.inventory.integration.dto;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class InventoryUpdateIntegrationTest extends AbstractTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    private String testBarcode;

    @Before
    public void setUp() {
        createTestClients();
        createTestProduct();
        createInitialInventory();
    }

    @Test
    public void testUpdateInventory() {
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(200);

        inventoryDto.updateInventoryByBarcode(testBarcode, form);

        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode(testBarcode, 0, 1);
        assertFalse(result.getContent().isEmpty());
        assertEquals(Integer.valueOf(200), result.getContent().get(0).getQuantity());
    }

    @Test
    public void testUpdateInventoryNonExistentBarcode() {
        InventoryForm form = new InventoryForm();
        form.setBarcode("NONEXISTENT");
        form.setQuantity(200);

        try {
            inventoryDto.updateInventoryByBarcode("NONEXISTENT", form);
            fail("Expected ApiException for non-existent barcode");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    public void testUpdateInventoryNegativeQuantity() {
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(-10);
        try {
            inventoryDto.updateInventoryByBarcode(testBarcode, form);
            fail("Expected ApiException for negative quantity");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("cannot be negative"));
        }
    }

    @Test
    public void testUpdateInventoryNullQuantity() {
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(null);
        try {
            inventoryDto.updateInventoryByBarcode(testBarcode, form);
            fail("Expected ApiException for null quantity");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("cannot be null"));
        }
    }

    private void createTestClients() {
        createClient("TestClient1");
    }

    private void createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        clientService.addClient(client);
    }

    private void createTestProduct() {
        testBarcode = "TEST001";
        ProductForm form = new ProductForm();
        form.setBarcode(testBarcode);
        form.setName("Test Product");
        form.setClientName("TestClient1");
        form.setMrp(100.0);
        productDto.addProduct(form);
    }

    private void createInitialInventory() {
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(100);
        inventoryDto.addInventory(form);
    }
} 