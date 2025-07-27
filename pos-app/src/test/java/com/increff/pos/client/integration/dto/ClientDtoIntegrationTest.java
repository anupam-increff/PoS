package com.increff.pos.client.integration.dto;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Test
    public void testAddClient_DtoServiceDaoIntegration() {
        // Given
        ClientForm clientForm = TestData.clientForm("Integration Test Client");

        // When - DTO integrates with Service and DAO
        clientDto.add(clientForm);

        // Then - Verify integration worked
        ClientPojo savedClient = clientDao.getClientByName("Integration Test Client");
        assertNotNull("Client should be saved through DTO->Service->DAO integration", savedClient);
        assertEquals("Integration Test Client", savedClient.getName());
    }

    @Test
    public void testGetAllClients_DtoServiceDaoIntegration() {
        // Given - Setup test data
        clientDao.insert(TestData.clientWithoutId("Client A"));
        clientDao.insert(TestData.clientWithoutId("Client B"));

        // When - DTO integrates with Service for pagination
        PaginatedResponse<ClientData> response = clientDto.getAll(0, 10);

        // Then - Verify DTO integration with Service and DAO
        assertNotNull("Response should be provided by DTO integration", response);
        assertEquals(2, response.getContent().size());
        assertTrue("Should contain Client A", 
            response.getContent().stream().anyMatch(c -> "Client A".equals(c.getName())));
        assertTrue("Should contain Client B", 
            response.getContent().stream().anyMatch(c -> "Client B".equals(c.getName())));
    }

    @Test
    public void testSearchClients_DtoServiceDaoIntegration() {
        // Given
        clientDao.insert(TestData.clientWithoutId("Search Client Alpha"));
        clientDao.insert(TestData.clientWithoutId("Search Client Beta"));
        clientDao.insert(TestData.clientWithoutId("Different Name"));

        // When - DTO integrates with Service for search
        PaginatedResponse<ClientData> response = clientDto.searchClients("Search", 0, 10);

        // Then - Verify search integration
        assertNotNull("Search results should be provided by DTO integration", response);
        assertEquals(2, response.getContent().size());
        assertTrue("All results should contain 'Search'", 
            response.getContent().stream().allMatch(c -> c.getName().contains("Search")));
    }
} 