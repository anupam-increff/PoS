package com.increff.pos.client.integration.dto;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for ClientDto.
 * Tests integration between ClientDto -> ClientService -> ClientDao
 */
public class ClientDtoIntegrationTest extends AbstractTest {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientDao clientDao;

    /**
     * Tests adding a new client through the complete integration stack.
     * Verifies DTO processes form data and persists client correctly.
     */
    @Test
    public void testAddClient() {
        // Given
        ClientForm clientForm = new ClientForm();
        clientForm.setName("Integration Test Client");

        // When - Add client through DTO
        clientDto.add(clientForm);

        // Then - Verify client was persisted
        ClientPojo dbClient = clientDao.getClientByName("Integration Test Client");
        assertNotNull("Client should be persisted in database", dbClient);
        assertEquals("Client name should match", "Integration Test Client", dbClient.getName());
    }

    /**
     * Tests retrieving all clients with pagination through the integration stack.
     * Verifies complete data flow from database to formatted response.
     */
    @Test
    public void testGetAllClients() {
        // Given - Create test clients in database
        ClientPojo client1 = TestData.clientWithoutId("Client One");
        ClientPojo client2 = TestData.clientWithoutId("Client Two");
        clientDao.insert(client1);
        clientDao.insert(client2);

        // When - Get all clients through DTO
        PaginatedResponse<ClientData> response = clientDto.getAll(0, 10);

        // Then - Verify integration results
        assertNotNull("Response should not be null", response);
        assertTrue("Should contain at least 2 clients", response.getContent().size() >= 2);
        assertTrue("Total should be at least 2", response.getTotalItems() >= 2);

        // Verify our test clients are included
        List<String> clientNames = response.getContent().stream()
            .map(ClientData::getName)
            .collect(java.util.stream.Collectors.toList());
        assertTrue("Should contain Client One", clientNames.contains("Client One"));
        assertTrue("Should contain Client Two", clientNames.contains("Client Two"));
    }

    /**
     * Tests searching clients by name through the integration stack.
     * Verifies search functionality with database queries and result formatting.
     */
    @Test
    public void testSearchClients() {
        // Given - Create searchable test clients
        ClientPojo testClient = TestData.clientWithoutId("Searchable Client");
        clientDao.insert(testClient);

        // When - Search through DTO
        PaginatedResponse<ClientData> response = clientDto.searchClients("Searchable", 0, 10);

        // Then - Verify search results
        assertNotNull("Search response should not be null", response);
        assertEquals("Should find exactly one client", 1, response.getContent().size());
        assertEquals("Found client name should match", "Searchable Client", response.getContent().get(0).getName());
    }
} 