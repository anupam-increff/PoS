package com.increff.pos.client.integration.dto;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientSearchIntegrationTest extends AbstractTest {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientService clientService;

    @Before
    public void setUp() {
        // Create test clients
        List<String> clientNames = Arrays.asList(
            "Apple Inc",
            "Apple Store",
            "Microsoft Corporation",
            "Google LLC",
            "Amazon.com"
        );

        for (String name : clientNames) {
            ClientForm form = new ClientForm();
            form.setName(name);
            clientDto.add(form);
        }
    }

    // Test get all clients
    @Test
    public void testGetAllClients() {
        PaginatedResponse<ClientData> response = clientDto.getAll(0, 10);
        assertEquals(5, response.getTotalItems());
        assertEquals(1, response.getTotalPages());
        assertEquals(5, response.getContent().size());
    }

    // Test get all clients with pagination
    @Test
    public void testGetAllClientsWithPagination() {
        PaginatedResponse<ClientData> response = clientDto.getAll(0, 2);
        assertEquals(5, response.getTotalItems());
        assertEquals(3, response.getTotalPages());
        assertEquals(2, response.getContent().size());
    }

    // Test search clients by query
    @Test
    public void testSearchClientsByQuery() {
        PaginatedResponse<ClientData> response = clientDto.searchClients("Apple", 0, 10);
        assertEquals(2, response.getTotalItems());
        List<String> names = response.getContent().stream()
                .map(ClientData::getName)
                .collect(Collectors.toList());
        assertTrue(names.contains("Apple Inc"));
        assertTrue(names.contains("Apple Store"));
    }

    // Test search clients case insensitive
    @Test
    public void testSearchClientsCaseInsensitive() {
        PaginatedResponse<ClientData> response = clientDto.searchClients("apple", 0, 10);
        assertEquals(2, response.getTotalItems());
    }

    // Test search clients with partial match
    @Test
    public void testSearchClientsPartialMatch() {
        PaginatedResponse<ClientData> response = clientDto.searchClients("Corp", 0, 10);
        assertEquals(1, response.getTotalItems());
        assertEquals("Microsoft Corporation", response.getContent().get(0).getName());
    }

    // Test search clients with no results
    @Test
    public void testSearchClientsNoResults() {
        PaginatedResponse<ClientData> response = clientDto.searchClients("XYZ", 0, 10);
        assertEquals(0, response.getTotalItems());
        assertEquals(0, response.getContent().size());
    }

    // Test search clients with empty query
    @Test
    public void testSearchClientsEmptyQuery() {
        PaginatedResponse<ClientData> response = clientDto.searchClients("", 0, 10);
        assertEquals(5, response.getTotalItems());
    }

    // Test search clients with pagination
    @Test
    public void testSearchClientsWithPagination() {
        PaginatedResponse<ClientData> response = clientDto.searchClients("", 0, 2);
        assertEquals(5, response.getTotalItems());
        assertEquals(3, response.getTotalPages());
        assertEquals(2, response.getContent().size());
    }
} 