package com.increff.pos.product.integration.dto;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for ProductDto.
 * Tests integration between ProductDto -> ProductFlow -> ProductService -> ProductDao
 */
public class ProductDtoIntegrationTest extends AbstractTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.clientWithoutId("Test Client");
        clientDao.insert(testClient);
    }

    @Test
    public void testAddProduct_DtoFlowServiceDaoIntegration() {
        // Given
        ProductForm productForm = TestData.productForm("BARCODE-001", "Test Product", "Test Client", 99.99);

        // When - DTO integrates through Flow -> Service -> DAO
        productDto.addProduct(productForm);

        // Then - Verify integration worked
        ProductPojo savedProduct = productDao.getByBarcode("BARCODE-001");
        assertNotNull("Product should be saved through DTO->Flow->Service->DAO integration", savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        assertEquals("BARCODE-001", savedProduct.getBarcode());
        assertEquals(testClient.getId(), savedProduct.getClientId());
    }

    @Test
    public void testGetByBarcode_DtoServiceIntegration() {
        // Given - Setup test data
        ProductPojo product = TestData.productWithoutId("BARCODE-002", "Test Product 2", testClient.getId());
        productDao.insert(product);

        // When - DTO integrates with Service
        ProductData result = productDto.getByBarcode("BARCODE-002");

        // Then - Verify DTO integration with Service
        assertNotNull("Product should be retrieved through DTO->Service integration", result);
        assertEquals("Test Product 2", result.getName());
        assertEquals("BARCODE-002", result.getBarcode());
    }
} 