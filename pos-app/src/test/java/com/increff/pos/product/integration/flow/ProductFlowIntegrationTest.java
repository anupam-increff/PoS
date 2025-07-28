package com.increff.pos.product.integration.flow;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.flow.ProductFlow;
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
 * Integration tests for ProductFlow.
 * Tests integration between ProductFlow -> ProductService + ClientService -> DAOs
 */
public class ProductFlowIntegrationTest extends AbstractTest {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.clientWithoutId("Test Client Flow");
        clientDao.insert(testClient);
    }

    @Test
    public void testAddProduct_FlowServiceIntegration() {
        // Given
        ProductPojo productPojo = TestData.productWithoutId("FLOW-001", "Flow Test Product", testClient.getId());

        // When - Flow orchestrates ProductService and ClientService
        productFlow.addProduct(productPojo, "Test Client Flow");

        // Then - Verify flow orchestration worked
        ProductPojo savedProduct = productDao.getByBarcode("FLOW-001");
        assertNotNull("Product should be saved through Flow->Service integration", savedProduct);
        assertEquals("Flow Test Product", savedProduct.getName());
        assertEquals(testClient.getId(), savedProduct.getClientId());
    }

    @Test
    public void testUpdateProduct_FlowServiceIntegration() {
        // Given - Setup existing product
        ProductPojo existingProduct = TestData.productWithoutId("FLOW-002", "Original Name", testClient.getId());
        productDao.insert(existingProduct);

        ProductPojo updateData = TestData.productWithoutId("FLOW-002", "Updated Name", testClient.getId());

        // When - Flow orchestrates update through services
        productFlow.updateProduct(existingProduct.getId(), updateData, "Test Client Flow");

        // Then - Verify flow orchestration worked
        ProductPojo updatedProduct = productDao.getById(existingProduct.getId());
        assertNotNull("Product should be updated through Flow->Service integration", updatedProduct);
        assertEquals("Updated Name", updatedProduct.getName());
    }
} 