package com.increff.pos.product.unit.flow;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    private ProductPojo product;
    private ClientPojo client;

    @Before
    public void setUp() {
        product = new ProductPojo();
        product.setBarcode("BARCODE-001");
        product.setName("Test Product");
        product.setMrp(99.99);

        client = new ClientPojo();
        client.setId(1);
        client.setName("Test Client");
    }

    @Test
    public void addProduct() {
        when(clientService.getCheckClientByName("Test Client")).thenReturn(client);
        productFlow.addProduct(product, "Test Client");
        assertEquals(Integer.valueOf(1), product.getClientId());
        verify(productService).addProduct(product);
    }

    @Test
    public void getProductsByAClient() {
        when(clientService.getCheckClientByName("Test Client")).thenReturn(client);
        when(productService.getProductsByClientId(1, 0, 10)).thenReturn(Arrays.asList(product));
        List<ProductPojo> result = productFlow.getProductsByAClient("Test Client", 0, 10);
        assertEquals(1, result.size());
        verify(productService).getProductsByClientId(1, 0, 10);
    }

    @Test
    public void countProductsByAClient() {
        when(clientService.getCheckClientByName("Test Client")).thenReturn(client);
        when(productService.countProductsByClientId(1)).thenReturn(5L);
        long count = productFlow.countProductsByAClient("Test Client");
        assertEquals(5L, count);
        verify(productService).countProductsByClientId(1);
    }

    @Test
    public void updateProduct() {
        when(clientService.getCheckClientByName("Test Client")).thenReturn(client);
        productFlow.updateProduct(1, product, "Test Client");
        assertEquals(Integer.valueOf(1), product.getClientId());
        verify(productService).update(1, product);
    }
} 