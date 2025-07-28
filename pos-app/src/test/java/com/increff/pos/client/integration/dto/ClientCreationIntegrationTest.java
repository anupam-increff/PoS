package com.increff.pos.client.integration.dto;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientCreationIntegrationTest extends AbstractTest {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientService clientService;

    // Test normal client creation
    @Test
    public void testCreateValidClient() {
        ClientForm form = new ClientForm();
        form.setName("Test Client");

        clientDto.add(form);

        // Verify using service layer
        ClientPojo client = clientService.getCheckClientByName("Test Client");
        assertNotNull(client);
        assertEquals("Test Client", client.getName());
    }

    // Test client creation with minimum required fields
    @Test
    public void testCreateClientMinimumFields() {
        ClientForm form = new ClientForm();
        form.setName("Test Client");

        clientDto.add(form);

        ClientPojo client = clientService.getCheckClientByName("Test Client");
        assertNotNull(client);
        assertEquals("Test Client", client.getName());
    }

    // Test client creation validation - null name
    @Test(expected = Exception.class)
    public void testCreateClientNullName() {
        ClientForm form = new ClientForm();
        clientDto.add(form);
    }

    // Test client creation validation - empty name
    @Test(expected = Exception.class)
    public void testCreateClientEmptyName() {
        ClientForm form = new ClientForm();
        form.setName("");
        clientDto.add(form);
    }

    // Test client creation validation - whitespace name
    @Test(expected = Exception.class)
    public void testCreateClientWhitespaceName() {
        ClientForm form = new ClientForm();
        form.setName("   ");
        clientDto.add(form);
    }

    // Test client creation validation - name too long
    @Test(expected = Exception.class)
    public void testCreateClientNameTooLong() {
        ClientForm form = new ClientForm();
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("A");
        }
        form.setName(longName.toString());
        clientDto.add(form);
    }

    // Test duplicate client name
    @Test(expected = Exception.class)
    public void testCreateDuplicateClient() {
        ClientForm form = new ClientForm();
        form.setName("Test Client");

        clientDto.add(form);
        clientDto.add(form); // Should throw exception
    }
} 