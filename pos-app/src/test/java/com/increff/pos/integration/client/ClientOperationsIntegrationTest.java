package com.increff.pos.integration.client;

import com.increff.pos.config.IntegrationTestConfig;
import com.increff.pos.config.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dto.ClientDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Integration tests for Client operations.
 * Tests client creation, retrieval, search, and validation scenarios.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestConfig.class})
@Transactional
public class ClientOperationsIntegrationTest {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientDao clientDao;

    @Test
    public void testAddClient_Success() {
        // Given: Valid client form
        ClientForm form = TestData.clientForm("TestClient");

        // When: Adding client
        clientDto.add(form);

        // Then: Client should be saved in database
        ClientPojo savedClient = clientDao.getClientByName("TestClient");
        assertNotNull("Client should be saved", savedClient);
        assertEquals("TestClient", savedClient.getName());
    }

    @Test(expected = ApiException.class)
    public void testAddClient_DuplicateName() {
        // Given: Two clients with same name
        ClientForm form1 = TestData.clientForm("DuplicateClient");
        ClientForm form2 = TestData.clientForm("DuplicateClient");

        // When: Adding first client (should succeed)
        clientDto.add(form1);

        // Then: Adding second client with same name should fail
        clientDto.add(form2);
    }

    @Test(expected = ApiException.class)
    public void testAddClient_NullName() {
        // Given: Client form with null name
        ClientForm form = TestData.clientForm(null);

        // When: Adding client with null name
        // Then: Should throw ApiException
        clientDto.add(form);
    }

    @Test(expected = ApiException.class)
    public void testAddClient_EmptyName() {
        // Given: Client form with empty name
        ClientForm form = TestData.clientForm("");

        // When: Adding client with empty name
        // Then: Should throw ApiException
        clientDto.add(form);
    }

    @Test
    public void testGetAllClients() {
        // Given: Multiple clients exist
        clientDto.add(TestData.clientForm("Client1"));
        clientDto.add(TestData.clientForm("Client2"));
        clientDto.add(TestData.clientForm("Client3"));

        // When: Getting all clients
        PaginatedResponse<ClientData> response = clientDto.getAll(0, 10);

        // Then: Should return all clients with pagination info
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 3 clients", 3, response.getContent().size());
        assertEquals("Total items should be 3", 3, response.getTotalItems());
    }

    @Test
    public void testSearchClientsByName() {
        // Given: Clients with different names
        clientDto.add(TestData.clientForm("Amazon"));
        clientDto.add(TestData.clientForm("Flipkart"));
        clientDto.add(TestData.clientForm("Amex"));

        // When: Searching for clients with "Am"
        PaginatedResponse<ClientData> response = clientDto.searchClients("Am", 0, 10);

        // Then: Should return clients containing "Am"
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 2 clients", 2, response.getContent().size());
        
        // Verify the returned clients contain "Am"
        response.getContent().forEach(client -> 
            assertTrue("Client name should contain 'Am'", 
                client.getName().toLowerCase().contains("am")));
    }

    @Test
    public void testSearchClientsWithPagination() {
        // Given: Multiple clients for pagination test
        for (int i = 1; i <= 15; i++) {
            clientDto.add(TestData.clientForm("Client" + i));
        }

        // When: Getting first page with size 5
        PaginatedResponse<ClientData> page1 = clientDto.getAll(0, 5);

        // Then: Should return correct pagination info
        assertNotNull("Page 1 should not be null", page1);
        assertEquals("Page 1 should have 5 clients", 5, page1.getContent().size());
        assertEquals("Total items should be 15", 15, page1.getTotalItems());
        assertEquals("Should have 3 total pages", 3, page1.getTotalPages());

        // When: Getting second page
        PaginatedResponse<ClientData> page2 = clientDto.getAll(1, 5);

        // Then: Should return different clients
        assertNotNull("Page 2 should not be null", page2);
        assertEquals("Page 2 should have 5 clients", 5, page2.getContent().size());
        
        // Verify pages have different content
        assertNotEquals("Pages should have different content",
            page1.getContent().get(0).getName(),
            page2.getContent().get(0).getName());
    }

    @Test
    public void testSearchClientsNotFound() {
        // Given: Some clients exist
        clientDto.add(TestData.clientForm("Amazon"));
        clientDto.add(TestData.clientForm("Flipkart"));

        // When: Searching for non-existent client
        PaginatedResponse<ClientData> response = clientDto.searchClients("NonExistent", 0, 10);

        // Then: Should return empty result
        assertNotNull("Response should not be null", response);
        assertEquals("Should have 0 clients", 0, response.getContent().size());
        assertEquals("Total items should be 0", 0, response.getTotalItems());
    }
} 