package com.increff.pos.unit.dto;

import com.increff.pos.config.TestData;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDtoTest {

    @Mock
    private ProductFlow productFlow;

    @InjectMocks
    private ProductDto productDto;

    private ProductForm testProductForm;

    @Before
    public void setUp() {
        testProductForm = TestData.productForm("BARCODE-001", "Test Product", "TestClient", 99.99);
    }

    @Test
    public void testAddProduct_Success() {
        // Given
        doNothing().when(productFlow).addProduct(any(ProductPojo.class), anyString());

        // When
        productDto.addProduct(testProductForm);

        // Then
        verify(productFlow, times(1)).addProduct(any(ProductPojo.class), eq("TestClient"));
    }

    @Test
    public void testAddProduct_FormToPojoConversion() {
        // Given
        doNothing().when(productFlow).addProduct(any(ProductPojo.class), anyString());

        // When
        productDto.addProduct(testProductForm);

        // Then - Verify that ProductFlow is called with converted POJO and client name
        verify(productFlow, times(1)).addProduct(any(ProductPojo.class), eq("TestClient"));
    }
} 