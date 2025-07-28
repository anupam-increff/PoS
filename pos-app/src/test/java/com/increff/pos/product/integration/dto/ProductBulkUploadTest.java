package com.increff.pos.product.integration.dto;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
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

public class ProductBulkUploadTest extends AbstractTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    @Before
    public void setUp() {
        createTestClients();
    }

    @Test
    public void uploadValidProducts() throws Exception {
        File file = ResourceUtils.getFile("classpath:test-data/valid_products_small.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "valid_products_small.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        TSVUploadResponse response = productDto.uploadProductMasterByTsv(multipartFile);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrorRows());

        ProductData product = productDto.getByBarcode("VALID0Q0");
        assertEquals("Headphones 1", product.getName());
        assertEquals("TestClient2", product.getClientName());
        assertEquals(8319.67, product.getMrp(), 0.01);
    }

    @Test
    public void uploadDuplicateBarcode() throws Exception {
        // First upload a valid product with barcode DUPLICATE1
        ProductForm form = new ProductForm();
        form.setBarcode("DUPLICATE1");
        form.setName("Original Product");
        form.setClientName("TestClient1");
        form.setMrp(50.0);
        productDto.addProduct(form);

        // Then try to upload file with duplicate barcodes
        File file = ResourceUtils.getFile("classpath:test-data/duplicate_products.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "duplicate_products.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        TSVUploadResponse response = productDto.uploadProductMasterByTsv(multipartFile);
        assertFalse(response.isSuccess());
        assertEquals(4, response.getErrorRows()); // 3 errors for DUPLICATE1 + 1 error for second DUPLICATE2
        assertTrue("Expected message to contain 'already exists' but was: " + response.getMessage(), 
                  response.getMessage().contains("already exists"));
    }

    @Test
    public void uploadEmptyFile() throws Exception {
        File file = ResourceUtils.getFile("classpath:test-data/empty_products.tsv");
        MultipartFile multipartFile = new MockMultipartFile("file", "empty_products.tsv", 
                "text/tab-separated-values", Files.readAllBytes(file.toPath()));

        try {
            productDto.uploadProductMasterByTsv(multipartFile);
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
            productDto.uploadProductMasterByTsv(multipartFile);
            fail("Expected ApiException for file with only headers");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("empty"));
        }
    }

    private void createTestClients() {
        createClient("TestClient1");
        createClient("TestClient2");
        createClient("TestClient3");
        createClient("SmallBiz");
        createClient("BigCorp");
    }

    private void createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        clientService.addClient(client);
    }
} 