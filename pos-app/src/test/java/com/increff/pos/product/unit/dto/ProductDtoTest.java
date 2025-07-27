package com.increff.pos.product.unit.dto;

import com.increff.pos.setup.TestData;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.ClientService;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ProductForm;
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

    @Test
    public void testAddProduct_Success() {
        // Given
        doNothing().when(productFlow).addProduct(any(ProductPojo.class), anyString());

        // When
        productDto.addProduct(testProductForm);

        // Then
        verify(productFlow, times(1)).addProduct(any(ProductPojo.class), eq(testProductForm.getClientName()));
    }

    @Test
    public void testGetAll_Success() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productFlow.getAllProducts(0, 10)).thenReturn(products);
        when(productFlow.countAllProducts()).thenReturn(1L);
        when(clientService.getCheckClientById(1)).thenReturn(testClient);

        // When
        PaginatedResponse<ProductData> result = productDto.getAll(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getName());
        assertEquals("BARCODE-001", result.getContent().get(0).getBarcode());
        verify(productFlow, times(1)).getAllProducts(0, 10);
        verify(productFlow, times(1)).countAllProducts();
    }

    @Test
    public void testGetByBarcode_Success() {
        // Given
        when(productFlow.getProductByBarcode("BARCODE-001")).thenReturn(testProduct);
        when(clientService.getCheckClientById(1)).thenReturn(testClient);

        // When
        ProductData result = productDto.getByBarcode("BARCODE-001");

        // Then
        assertNotNull(result);
        assertEquals("BARCODE-001", result.getBarcode());
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getMrp(), 0.01);
        verify(productFlow, times(1)).getProductByBarcode("BARCODE-001");
    }

    @Test
    public void testUpdate_Success() {
        // Given
        doNothing().when(productFlow).updateProduct(anyInt(), any(ProductPojo.class), anyString());

        // When
        productDto.update(1, testProductForm);

        // Then
        verify(productFlow, times(1)).updateProduct(eq(1), any(ProductPojo.class), eq(testProductForm.getClientName()));
    }

    @Test
    public void testSearchByBarcode_Success() {
        // Given
        List<ProductPojo> products = Arrays.asList(testProduct);
        when(productFlow.searchProductsByBarcode(anyString(), anyInt(), anyInt())).thenReturn(products);
        when(productFlow.countSearchByBarcode(anyString())).thenReturn(1L);
        when(clientService.getCheckClientById(1)).thenReturn(testClient);

        // When
        PaginatedResponse<ProductData> result = productDto.searchByBarcode("BAR", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("BARCODE-001", result.getContent().get(0).getBarcode());
        verify(productFlow, times(1)).searchProductsByBarcode("BAR", 0, 10);
        verify(productFlow, times(1)).countSearchByBarcode("BAR");
    }
} 