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

public class ProductCreationTest extends AbstractTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientService clientService;

    private Validator validator;
    private ProductForm form;
    private ClientPojo client;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        form = new ProductForm();
        form.setBarcode("BARCODE-001");
        form.setName("Test Product");
        form.setClientName("Test Client");
        form.setMrp(99.99);

        client = new ClientPojo();
        client.setName("Test Client");
        clientService.addClient(client);
    }

    @Test
    public void validateNullBarcode() {
        form.setBarcode(null);
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateEmptyBarcode() {
        form.setBarcode("");
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateWhitespaceBarcode() {
        form.setBarcode("   ");
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateNullName() {
        form.setName(null);
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateEmptyName() {
        form.setName("");
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateWhitespaceName() {
        form.setName("   ");
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateNegativeMrp() {
        form.setMrp(-1.0);
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validateZeroMrp() {
        form.setMrp(0.0);
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createProduct() {
        productDto.addProduct(form);
        ProductData product = productDto.getByBarcode("BARCODE-001");
        assertEquals("Test Product", product.getName());
        assertEquals("Test Client", product.getClientName());
        assertEquals(99.99, product.getMrp(), 0.01);
    }

    @Test
    public void createDuplicateBarcode() {
        productDto.addProduct(form);
        
        ProductForm duplicateForm = new ProductForm();
        duplicateForm.setBarcode("BARCODE-001");
        duplicateForm.setName("Another Product");
        duplicateForm.setClientName("Test Client");
        duplicateForm.setMrp(149.99);

        try {
            productDto.addProduct(duplicateForm);
            fail("Expected ApiException for duplicate barcode");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("already exists"));
        }
    }
} 