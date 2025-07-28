package com.increff.pos.product.integration.dto;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.*;

public class ProductUpdateTest extends AbstractTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    private Validator validator;
    private ProductForm form;
    private ProductData product;
    private ClientPojo client;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        client = new ClientPojo();
        client.setName("Test Client");
        clientService.addClient(client);

        form = new ProductForm();
        form.setBarcode("BARCODE-001");
        form.setName("Test Product");
        form.setClientName("Test Client");
        form.setMrp(99.99);
        
        productDto.addProduct(form);
        product = productDto.getByBarcode("BARCODE-001");
    }

    @Test
    public void updateName() {
        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode("BARCODE-001");
        updateForm.setName("Updated Product");
        updateForm.setClientName("Test Client");
        updateForm.setMrp(99.99);

        productDto.update(product.getId(), updateForm);
        ProductData updated = productDto.getByBarcode("BARCODE-001");
        assertEquals("Updated Product", updated.getName());
    }

    @Test
    public void updateMrp() {
        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode("BARCODE-001");
        updateForm.setName("Test Product");
        updateForm.setClientName("Test Client");
        updateForm.setMrp(149.99);

        productDto.update(product.getId(), updateForm);
        ProductData updated = productDto.getByBarcode("BARCODE-001");
        assertEquals(149.99, updated.getMrp(), 0.01);
    }

    @Test
    public void updateClient() {
        ClientPojo newClient = new ClientPojo();
        newClient.setName("Another Client");
        clientService.addClient(newClient);

        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode("BARCODE-001");
        updateForm.setName("Test Product");
        updateForm.setClientName("Another Client");
        updateForm.setMrp(99.99);

        productDto.update(product.getId(), updateForm);
        ProductData updated = productDto.getByBarcode("BARCODE-001");
        assertEquals("Another Client", updated.getClientName());
    }

    @Test
    public void updateNonExistentProduct() {
        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode("BARCODE-001");
        updateForm.setName("Updated Product");
        updateForm.setClientName("Test Client");
        updateForm.setMrp(149.99);

        try {
            productDto.update(999, updateForm);
            fail("Expected ApiException for non-existent product");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    public void validateUpdateNullName() {
        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode("BARCODE-001");
        updateForm.setName(null);
        updateForm.setClientName("Test Client");
        updateForm.setMrp(99.99);

        Set<ConstraintViolation<ProductForm>> violations = validator.validate(updateForm);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateUpdateNegativeMrp() {
        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode("BARCODE-001");
        updateForm.setName("Test Product");
        updateForm.setClientName("Test Client");
        updateForm.setMrp(-1.0);

        Set<ConstraintViolation<ProductForm>> violations = validator.validate(updateForm);
        assertFalse(violations.isEmpty());
    }
} 