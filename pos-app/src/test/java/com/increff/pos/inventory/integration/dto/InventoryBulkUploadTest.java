package com.increff.pos.inventory.integration.dto;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;
import com.increff.pos.model.data.PaginatedResponse;

public class InventoryBulkUploadTest extends AbstractTest {

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
    }

    @Test
    public void uploadValidInventory() throws Exception {
        File file = ResourceUtils.getFile("classpath:test-data/valid_inventory_small.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "valid_inventory_small.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        TSVUploadResponse response = inventoryDto.uploadInventoryByTsv(multipartFile);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrorRows());

        PaginatedResponse<InventoryData> result = inventoryDto.searchByBarcode("VALID0Q0", 0, 1);
        assertFalse(result.getContent().isEmpty());
        assertEquals(Integer.valueOf(100), result.getContent().get(0).getQuantity());
    }

    @Test
    public void uploadNonExistentBarcode() throws Exception {
        File file = ResourceUtils.getFile("classpath:test-data/error_inventory.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "error_inventory.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        TSVUploadResponse response = inventoryDto.uploadInventoryByTsv(multipartFile);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    public void uploadEmptyFile() throws Exception {
        File file = ResourceUtils.getFile("classpath:test-data/empty_products.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "empty_products.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        try {
            inventoryDto.uploadInventoryByTsv(multipartFile);
            fail("Expected ApiException for empty file");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("empty"));
        }
    }

    @Test
    public void uploadHeadersOnly() throws Exception {
        File file = ResourceUtils.getFile("classpath:test-data/headers_only_products.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "headers_only_products.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        try {
            inventoryDto.uploadInventoryByTsv(multipartFile);
            fail("Expected ApiException for file with only headers");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("empty"));
        }
    }

    @Test
    public void uploadInvalidQuantity() throws Exception {
        // Create a test file with negative quantity
        String content = "barcode\tquantity\nVALID0Q0\t-10\n";
        MultipartFile multipartFile = new MockMultipartFile("file", "invalid_quantity.tsv", 
                "text/tab-separated-values", content.getBytes());

        TSVUploadResponse response = inventoryDto.uploadInventoryByTsv(multipartFile);
        assertFalse(response.isSuccess());
        assertTrue("Expected message to contain 'Minimum quantity is at least 1' but was: " + response.getMessage(),
                  response.getMessage().contains("Minimum quantity is at least 1"));
    }

    private void createTestClients() {
        createClient("TestClient1");
        createClient("TestClient2");
        createClient("TestClient3");
    }

    private void createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        clientService.addClient(client);
    }

    private void createTestProducts() {
        createProduct("VALID0Q0", "Test Product 1", "TestClient1", 100.0);
        createProduct("VALID0Q1", "Test Product 2", "TestClient2", 200.0);
        createProduct("VALID0Q2", "Test Product 3", "TestClient3", 300.0);
    }

    private void createProduct(String barcode, String name, String clientName, Double mrp) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setClientName(clientName);
        form.setMrp(mrp);
        productDto.addProduct(form);
    }
} 