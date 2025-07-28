package com.increff.pos.inventory.integration.dto;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
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

public class InventorySearchIntegrationTest extends AbstractTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    @Before
    public void setUp() {
        createTestClients();
        createTestProducts();
        createTestInventories();
    }

    @Test
    public void testGetAll() {
        PaginatedResponse<InventoryData> response = inventoryDto.getAll(0, 10);
        assertEquals(3L, response.getTotalItems());
        assertEquals(3, response.getContent().size());
    }

    @Test
    public void testGetAllPagination() {
        PaginatedResponse<InventoryData> response = inventoryDto.getAll(0, 2);
        assertEquals(3L, response.getTotalItems());
        assertEquals(2, response.getContent().size());
        assertEquals(2, response.getTotalPages());

        response = inventoryDto.getAll(1, 2);
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testSearchByBarcode() {
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("TEST001", 0, 10);
        assertEquals(1L, response.getTotalItems());
        assertEquals("TEST001", response.getContent().get(0).getBarcode());
        assertEquals(Integer.valueOf(100), response.getContent().get(0).getQuantity());
    }

    @Test
    public void testSearchByBarcodePartial() {
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("TEST", 0, 10);
        assertEquals(3L, response.getTotalItems());
        assertTrue(response.getContent().stream().allMatch(i -> i.getBarcode().startsWith("TEST")));
    }

    @Test
    public void testSearchByBarcodeNoMatch() {
        PaginatedResponse<InventoryData> response = inventoryDto.searchByBarcode("NONEXISTENT", 0, 10);
        assertEquals(0L, response.getTotalItems());
        assertTrue(response.getContent().isEmpty());
    }

    private void createTestClients() {
        createClient("TestClient1");
    }

    private void createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        clientService.addClient(client);
    }

    private void createTestProducts() {
        createProduct("TEST001", "Test Product 1", "TestClient1", 100.0);
        createProduct("TEST002", "Test Product 2", "TestClient1", 200.0);
        createProduct("TEST003", "Test Product 3", "TestClient1", 300.0);
    }

    private void createProduct(String barcode, String name, String clientName, Double mrp) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setClientName(clientName);
        form.setMrp(mrp);
        productDto.addProduct(form);
    }

    private void createTestInventories() {
        createInventory("TEST001", 100);
        createInventory("TEST002", 200);
        createInventory("TEST003", 300);
    }

    private void createInventory(String barcode, Integer quantity) {
        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);
        inventoryDto.addInventory(form);
    }
} 