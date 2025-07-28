package com.increff.pos.client.integration.dto;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientUpdateIntegrationTest extends AbstractTest {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientService clientService;

    private Integer clientId;

    @Before
    public void setUp() {
        // Create a client for testing updates
        ClientForm form = new ClientForm();
        form.setName("Original Client");
        clientDto.add(form);
        clientId = clientService.getCheckClientByName("Original Client").getId();
    }

    // Test normal client update
    @Test
    public void testUpdateValidClient() {
        ClientForm form = new ClientForm();
        form.setName("Updated Client");

        clientDto.update(clientId, form);

        // Verify using service layer
        ClientPojo client = clientService.getCheckClientById(clientId);
        assertNotNull(client);
        assertEquals("Updated Client", client.getName());
    }

    // Test client update validation - null name
    @Test(expected = ApiException.class)
    public void testUpdateClientNullName() {
        ClientForm form = new ClientForm();
        clientDto.update(clientId, form);
    }

    // Test client update validation - empty name
    @Test(expected = ApiException.class)
    public void testUpdateClientEmptyName() {
        ClientForm form = new ClientForm();
        form.setName("");
        clientDto.update(clientId, form);
    }

    // Test client update validation - whitespace name
    @Test(expected = ApiException.class)
    public void testUpdateClientWhitespaceName() {
        ClientForm form = new ClientForm();
        form.setName("   ");
        clientDto.update(clientId, form);
    }

    // Test client update validation - name too long
    @Test(expected = ApiException.class)
    public void testUpdateClientNameTooLong() {
        ClientForm form = new ClientForm();
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("A");
        }
        form.setName(longName.toString());
        clientDto.update(clientId, form);
    }

    // Test update with non-existent ID
    @Test(expected = ApiException.class)
    public void testUpdateNonExistentClient() {
        ClientForm form = new ClientForm();
        form.setName("Updated Client");
        clientDto.update(9999, form);
    }

    // Test update to duplicate name
    @Test(expected = ApiException.class)
    public void testUpdateToDuplicateName() {
        // Create another client first
        ClientForm otherForm = new ClientForm();
        otherForm.setName("Other Client");
        clientDto.add(otherForm);

        // Try to update original client to have the same name
        ClientForm updateForm = new ClientForm();
        updateForm.setName("Other Client");
        clientDto.update(clientId, updateForm);
    }
} 