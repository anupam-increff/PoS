package com.increff.pos.product.unit.flow;

import com.increff.pos.flow.ProductFlow;
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
public class ProductFlowTest {

    @Mock
    private ProductService productService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ProductFlow productFlow;

    private ProductPojo testProduct;
    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.client(1);
        testClient.setName("Test Client");
        
        testProduct = TestData.product(1, 1);
        testProduct.setBarcode("FLOW-001");
        testProduct.setName("Flow Test Product");
    }

    /**
     * Tests adding a product through the flow layer with client validation.
     * Verifies proper client lookup and product persistence.
     */
    @Test
    public void testAddProduct() {
        // Given
        when(clientService.getCheckClientByName("Test Client")).thenReturn(testClient);
        doNothing().when(productService).addProduct(any(ProductPojo.class));

        // When
        productFlow.addProduct(testProduct, "Test Client");

        // Then
        verify(clientService, times(1)).getCheckClientByName("Test Client");
        verify(productService, times(1)).addProduct(any(ProductPojo.class));
        assertEquals(testClient.getId(), testProduct.getClientId());
    }

    /**
     * Tests updating an existing product through the flow layer.
     * Verifies client validation and product update logic.
     */
    @Test
    public void testUpdateProduct() {
        // Given
        ProductPojo existingProduct = TestData.product(1, 1);
        when(clientService.getCheckClientByName("Test Client")).thenReturn(testClient);
        // ProductFlow.updateProduct calls productService.update, not getCheckProductById
        doNothing().when(productService).update(anyInt(), any(ProductPojo.class));

        // When
        productFlow.updateProduct(1, testProduct, "Test Client");

        // Then
        verify(clientService, times(1)).getCheckClientByName("Test Client");
        verify(productService, times(1)).update(eq(1), any(ProductPojo.class));
        assertEquals(testClient.getId(), testProduct.getClientId());
    }

    /**
     * Tests retrieving all products with pagination through the flow layer.
     * Verifies proper service delegation and data handling.
     */
    @Test
    public void testGetAllProducts() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productService.getAll(0, 10)).thenReturn(products);

        // When
        List<ProductPojo> result = productFlow.getAllProducts(0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one product", 1, result.size());
        assertEquals("Product should match", testProduct, result.get(0));
        verify(productService, times(1)).getAll(0, 10);
    }

    /**
     * Tests counting all products through the flow layer.
     * Verifies proper service delegation for count operations.
     */
    @Test
    public void testCountAllProducts() {
        // Given
        when(productService.countAll()).thenReturn(5L);

        // When
        long count = productFlow.countAllProducts();

        // Then
        assertEquals("Count should match service result", 5L, count);
        verify(productService, times(1)).countAll();
    }

    /**
     * Tests searching products by barcode through the flow layer.
     * Verifies search functionality and proper service integration.
     */
    @Test
    public void testSearchProductsByBarcode() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productService.searchByBarcode("FLOW", 0, 10)).thenReturn(products);

        // When
        List<ProductPojo> result = productFlow.searchProductsByBarcode("FLOW", 0, 10);

        // Then
        assertNotNull("Search result should not be null", result);
        assertEquals("Should find one product", 1, result.size());
        assertEquals("Found product should match", testProduct, result.get(0));
        verify(productService, times(1)).searchByBarcode("FLOW", 0, 10);
    }

    /**
     * Tests retrieving a product by barcode through the flow layer.
     * Verifies single product lookup functionality.
     */
    @Test
    public void testGetProductByBarcode() {
        // Given
        when(productService.getCheckProductByBarcode("FLOW-001")).thenReturn(testProduct);

        // When
        ProductPojo result = productFlow.getProductByBarcode("FLOW-001");

        // Then
        assertNotNull("Product should not be null", result);
        assertEquals("Product should match", testProduct, result);
        verify(productService, times(1)).getCheckProductByBarcode("FLOW-001");
    }
} 