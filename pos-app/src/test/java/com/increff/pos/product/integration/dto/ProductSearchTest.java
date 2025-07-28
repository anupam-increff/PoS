package com.increff.pos.product.integration.dto;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ProductSearchTest extends AbstractTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    private ClientPojo client;

    @Before
    public void setUp() {
        client = new ClientPojo();
        client.setName("Test Client");
        clientService.addClient(client);

        createProduct("BARCODE-001", "First Product", 99.99);
        createProduct("BARCODE-002", "Second Product", 149.99);
        createProduct("DIFFERENT-001", "Third Product", 199.99);
    }

    @Test
    public void searchByBarcode() {
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("BARCODE", 0, 10);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalItems());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getBarcode().equals("BARCODE-001")));
        assertTrue(result.getContent().stream().anyMatch(p -> p.getBarcode().equals("BARCODE-002")));
    }

    @Test
    public void searchByBarcodeNoMatch() {
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("NONEXISTENT", 0, 10);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalItems());
    }

    @Test
    public void searchByBarcodeExactMatch() {
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("BARCODE-001", 0, 10);
        assertEquals(1, result.getContent().size());
        assertEquals("BARCODE-001", result.getContent().get(0).getBarcode());
        assertEquals("First Product", result.getContent().get(0).getName());
    }

    @Test
    public void searchByBarcodeWithPagination() {
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("BARCODE", 0, 1);
        assertEquals(1, result.getContent().size());
        assertEquals(2L, result.getTotalItems());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    public void getByClient() {
        ClientPojo anotherClient = new ClientPojo();
        anotherClient.setName("Another Client");
        clientService.addClient(anotherClient);

        createProduct("BARCODE-003", "Fourth Product", 299.99, "Another Client");

        PaginatedResponse<ProductData> result = productDto.getByClient("Test Client", 0, 10);
        assertEquals(3, result.getContent().size());
        assertEquals(3L, result.getTotalItems());
        assertTrue(result.getContent().stream().allMatch(p -> p.getClientName().equals("Test Client")));
    }

    @Test
    public void getByClientNoProducts() {
        ClientPojo newClient = new ClientPojo();
        newClient.setName("New Client");
        clientService.addClient(newClient);

        PaginatedResponse<ProductData> result = productDto.getByClient("New Client", 0, 10);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalItems());
    }

    @Test
    public void getAllProducts() {
        PaginatedResponse<ProductData> result = productDto.getAll(0, 10);
        assertEquals(3, result.getContent().size());
        assertEquals(3L, result.getTotalItems());
    }

    @Test
    public void getAllProductsWithPagination() {
        PaginatedResponse<ProductData> result = productDto.getAll(0, 2);
        assertEquals(2, result.getContent().size());
        assertEquals(3L, result.getTotalItems());
        assertEquals(2, result.getTotalPages());
    }

    private void createProduct(String barcode, String name, double mrp) {
        createProduct(barcode, name, mrp, "Test Client");
    }

    private void createProduct(String barcode, String name, double mrp, String clientName) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setClientName(clientName);
        form.setMrp(mrp);
        productDto.addProduct(form);
    }
} 