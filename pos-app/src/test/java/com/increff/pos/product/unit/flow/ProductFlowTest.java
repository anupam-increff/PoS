package com.increff.pos.product.unit.flow;

import com.increff.pos.setup.TestData;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.ClientService;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
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

    @Test
    public void testAddProduct_Success() {
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

    @Test
    public void testUpdateProduct_Success() {
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

    @Test
    public void testGetAllProducts_Success() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productService.getAll(0, 10)).thenReturn(products);

        // When
        List<ProductPojo> result = productFlow.getAllProducts(0, 10);

        // Then
        assertEquals(products, result);
        verify(productService, times(1)).getAll(0, 10);
    }

    @Test
    public void testCountAllProducts_Success() {
        // Given
        when(productService.countAll()).thenReturn(5L);

        // When
        long count = productFlow.countAllProducts();

        // Then
        assertEquals(5L, count);
        verify(productService, times(1)).countAll();
    }

    @Test
    public void testGetProductByBarcode_Success() {
        // Given
        when(productService.getCheckProductByBarcode("FLOW-001")).thenReturn(testProduct);

        // When
        ProductPojo result = productFlow.getProductByBarcode("FLOW-001");

        // Then
        assertEquals(testProduct, result);
        verify(productService, times(1)).getCheckProductByBarcode("FLOW-001");
    }

    @Test
    public void testSearchProductsByBarcode_Success() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productService.searchByBarcode("FLOW", 0, 10)).thenReturn(products);

        // When
        List<ProductPojo> result = productFlow.searchProductsByBarcode("FLOW", 0, 10);

        // Then
        assertEquals(products, result);
        verify(productService, times(1)).searchByBarcode("FLOW", 0, 10);
    }
} 