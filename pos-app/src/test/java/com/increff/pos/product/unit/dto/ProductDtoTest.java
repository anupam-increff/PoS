package com.increff.pos.product.unit.dto;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDtoTest {

    @Mock
    private ProductFlow productFlow;

    @Mock
    private ProductService productService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ProductDto productDto;

    private ProductPojo testProduct;
    private ProductForm testProductForm;
    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.client(1);
        testClient.setName("TestClient");
        
        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("BARCODE-001");
        testProduct.setName("Test Product");
        testProduct.setMrp(99.99);
        testProduct.setClientId(1);
        
        testProductForm = TestData.productForm("BARCODE-001", "Test Product", "TestClient", 99.99);
    }

    /**
     * Tests adding a product successfully through the DTO layer.
     * Verifies proper form validation and flow integration.
     */
    @Test
    public void testAddProduct() {
        // Given
        doNothing().when(productFlow).addProduct(any(ProductPojo.class), anyString());

        // When
        productDto.addProduct(testProductForm);

        // Then
        verify(productFlow, times(1)).addProduct(any(ProductPojo.class), eq(testProductForm.getClientName()));
    }

    /**
     * Tests updating an existing product through the DTO layer.
     * Verifies proper form validation and flow integration for updates.
     */
    @Test
    public void testUpdateProduct() {
        // Given
        doNothing().when(productFlow).updateProduct(anyInt(), any(ProductPojo.class), anyString());

        // When
        productDto.update(1, testProductForm);

        // Then
        verify(productFlow, times(1)).updateProduct(eq(1), any(ProductPojo.class), eq(testProductForm.getClientName()));
    }

    /**
     * Tests retrieving product by barcode through the DTO layer.
     * Verifies proper data conversion and service integration.
     */
    @Test
    public void testGetProductByBarcode() {
        // Given
        when(productFlow.getProductByBarcode("BARCODE-001")).thenReturn(testProduct);
        when(clientService.getCheckClientById(1)).thenReturn(testClient);

        // When
        ProductData result = productDto.getByBarcode("BARCODE-001");

        // Then
        assertNotNull("Product data should not be null", result);
        assertEquals("Barcode should match", "BARCODE-001", result.getBarcode());
        assertEquals("Product name should match", "Test Product", result.getName());
        assertEquals("Client name should be populated", "TestClient", result.getClientName());
        verify(productFlow, times(1)).getProductByBarcode("BARCODE-001");
        verify(clientService, times(1)).getCheckClientById(1);
    }

    /**
     * Tests retrieving all products with pagination through the DTO layer.
     * Verifies proper pagination and data conversion.
     */
    @Test
    public void testGetAllProducts() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productFlow.getAllProducts(0, 10)).thenReturn(products);
        when(productFlow.countAllProducts()).thenReturn(1L);
        when(clientService.getCheckClientById(1)).thenReturn(testClient);

        // When
        PaginatedResponse<ProductData> result = productDto.getAll(0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one product", 1, result.getContent().size());
        assertEquals("Total items should be 1", 1L, result.getTotalItems());
        
        ProductData productData = result.getContent().get(0);
        assertEquals("Product name should match", "Test Product", productData.getName());
        assertEquals("Client name should be populated", "TestClient", productData.getClientName());
    }

    /**
     * Tests searching products by barcode pattern through the DTO layer.
     * Verifies search functionality and data conversion.
     */
    @Test
    public void testSearchProductsByBarcode() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productFlow.searchProductsByBarcode("BARCODE", 0, 10)).thenReturn(products);
        when(productFlow.countSearchByBarcode("BARCODE")).thenReturn(1L);
        when(clientService.getCheckClientById(1)).thenReturn(testClient);

        // When
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("BARCODE", 0, 10);

        // Then
        assertNotNull("Search result should not be null", result);
        assertEquals("Should find one product", 1, result.getContent().size());
        assertEquals("Total items should be 1", 1L, result.getTotalItems());
        
        ProductData productData = result.getContent().get(0);
        assertEquals("Product name should match", "Test Product", productData.getName());
        assertEquals("Client name should be populated", "TestClient", productData.getClientName());
    }
} 