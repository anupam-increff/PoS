package com.increff.pos.unit.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
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
public class ProductFlowTest {

    @InjectMocks
    private ProductFlow productFlow;

    @Mock
    private ProductService productService;

    @Mock
    private ClientService clientService;

    @Mock
    private TSVDownloadService tsvDownloadService;
    // No need to mock BulkUploadService as bulk upload is not tested here

    private ClientPojo client1;
    private ClientPojo client2;
    private ProductPojo product1;
    private ProductPojo product2;
    private ProductPojo product3;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        client1 = TestData.client(1);
        client2 = TestData.client(2);
        product1 = TestData.product(1, client1.getId());
        product2 = TestData.product(2, client1.getId());
        product3 = TestData.product(3, client2.getId());
    }

    @Test
    public void testAddProduct() throws ApiException {
        ProductForm form = TestData.productForm("BARCODE-1", "Test Product", client1.getName(), 99.99);
        ProductPojo productPojo = TestData.productWithoutId(form.getBarcode(), form.getName(), 0); // clientId will be set by flow
        productPojo.setMrp(form.getMrp());
        productPojo.setImageUrl(form.getImageUrl());
        when(clientService.getCheckClientByName(client1.getName())).thenReturn(client1);
        doNothing().when(productService).addProduct(any(ProductPojo.class));
        productFlow.addProduct(productPojo, client1.getName());
        verify(productService, times(1)).addProduct(any(ProductPojo.class));
    }

    @Test
    public void testAddProductWithNonExistentClient() {
        ProductForm form = TestData.productForm("BARCODE-1", "Test Product", "NonExistentClient", 99.99);
        ProductPojo productPojo = TestData.productWithoutId(form.getBarcode(), form.getName(), 0);
        productPojo.setMrp(form.getMrp());
        productPojo.setImageUrl(form.getImageUrl());
        when(clientService.getCheckClientByName("NonExistentClient")).thenThrow(new ApiException("Client not found"));
        try {
            productFlow.addProduct(productPojo, "NonExistentClient");
            fail("Should throw ApiException for non-existent client");
        } catch (ApiException e) {
            assertTrue(e.getMessage().toLowerCase().contains("client"));
        }
    }

    @Test
    public void testAddProductWithDuplicateBarcode() throws ApiException {
        ProductForm form = TestData.productForm("BARCODE-1", "Product 1", client1.getName(), 99.99);
        ProductPojo productPojo = TestData.productWithoutId(form.getBarcode(), form.getName(), 0);
        productPojo.setMrp(form.getMrp());
        productPojo.setImageUrl(form.getImageUrl());
        when(clientService.getCheckClientByName(client1.getName())).thenReturn(client1);
        doThrow(new ApiException("Product with same barcode : BARCODE-1 already exists !")).when(productService).addProduct(any(ProductPojo.class));
        try {
            productFlow.addProduct(productPojo, client1.getName());
            fail("Should throw ApiException for duplicate barcode");
        } catch (ApiException e) {
            assertTrue(e.getMessage().toLowerCase().contains("barcode"));
        }
    }

    @Test
    public void testGetAllProducts() {
        when(productService.getAll(0, 10)).thenReturn(Arrays.asList(product1, product2));
        when(clientService.getCheckClientById(client1.getId())).thenReturn(client1);
        List<ProductData> result = productFlow.getAllProducts(0, 10);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetProductsByClient() {
        when(clientService.getCheckClientByName(client1.getName())).thenReturn(client1);
        when(productService.getProductsByClientId(client1.getId(), 0, 10)).thenReturn(Arrays.asList(product1, product2));
        when(clientService.getCheckClientById(client1.getId())).thenReturn(client1);
        List<ProductData> result = productFlow.getProductsByAClient(client1.getName(), 0, 10);
        assertNotNull(result);
        assertEquals(2, result.size());
        for (ProductData product : result) {
            assertEquals(client1.getName(), product.getClientName());
        }
    }

    @Test
    public void testSearchProductsByBarcode() {
        when(productService.searchByBarcode("BARCODE", 0, 10)).thenReturn(Arrays.asList(product1, product2));
        when(clientService.getCheckClientById(client1.getId())).thenReturn(client1);
        List<ProductData> result = productFlow.searchProductsByBarcode("BARCODE", 0, 10);
        assertNotNull(result);
        assertEquals(2, result.size());
        for (ProductData product : result) {
            assertTrue(product.getBarcode().toLowerCase().contains("barcode"));
        }
    }

    @Test
    public void testGetProductByBarcode() {
        when(productService.getCheckProductByBarcode("BARCODE-1")).thenReturn(product1);
        when(clientService.getCheckClientById(client1.getId())).thenReturn(client1);
        ProductData result = productFlow.getProductByBarcode("BARCODE-1");
        assertNotNull(result);
        assertEquals("Product-1", result.getName());
        assertEquals("BARCODE-1", result.getBarcode());
    }

    @Test
    public void testGetProductByBarcodeNotFound() {
        when(productService.getCheckProductByBarcode("NONEXISTENT")).thenThrow(new ApiException("No product with barcode : NONEXISTENT exists"));
        try {
            productFlow.getProductByBarcode("NONEXISTENT");
            fail("Should throw ApiException for non-existent product");
        } catch (ApiException e) {
            assertTrue(e.getMessage().toLowerCase().contains("product"));
        }
    }

    @Test
    public void testUpdateProduct() throws ApiException {
        ProductForm form = TestData.productForm("BARCODE-1", "Updated Product", client1.getName(), 199.99);
        when(clientService.getCheckClientByName(client1.getName())).thenReturn(client1);
        doNothing().when(productService).update(anyInt(), any(ProductPojo.class));
        productFlow.updateProduct(1, form);
        verify(productService, times(1)).update(anyInt(), any(ProductPojo.class));
    }

    @Test
    public void testUpdateProductNotFound() throws ApiException {
        ProductForm form = TestData.productForm("BARCODE-1", "Updated Product", client1.getName(), 199.99);
        when(clientService.getCheckClientByName(client1.getName())).thenReturn(client1);
        doThrow(new ApiException("No such product exists")).when(productService).update(anyInt(), any(ProductPojo.class));
        try {
            productFlow.updateProduct(1, form);
            fail("Should throw ApiException for non-existent product");
        } catch (ApiException e) {
            assertTrue(e.getMessage().toLowerCase().contains("product"));
        }
    }

    @Test
    public void testCountAllProducts() {
        when(productService.countAll()).thenReturn(2L);
        long count = productFlow.countAllProducts();
        assertEquals(2L, count);
    }

    @Test
    public void testCountProductsByClient() {
        when(clientService.getCheckClientByName(client1.getName())).thenReturn(client1);
        when(productService.countProductsByClientId(client1.getId())).thenReturn(2L);
        long count = productFlow.countProductsByAClient(client1.getName());
        assertEquals(2L, count);
    }

    @Test
    public void testCountSearchByBarcode() {
        when(productService.countSearchByBarcode("BARCODE")).thenReturn(2L);
        long count = productFlow.countSearchByBarcode("BARCODE");
        assertEquals(2L, count);
    }

    // Bulk upload tests are not present here; they are covered in integration tests.
} 