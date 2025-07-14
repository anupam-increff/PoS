package com.increff.pos.integration.dto.client;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.dto.ClientDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.increff.pos.setup.IntegrationTestConfig.class})
@Transactional
public class ClientCreationIntegrationTests {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientDao clientDao;

    private static int testCounter = 0;

    @Before
    public void setUp() {
        // Clean up any existing data
        // This will be handled by @Transactional rollback
    }

    private String getUniqueClientName(String baseName) {
        return baseName + "_" + System.currentTimeMillis() + "_" + (++testCounter);
    }

    @Test
    public void testAddClient() throws ApiException {
        // Arrange
        String uniqueClientName = getUniqueClientName("TestClient");
        ClientForm form = TestData.clientForm(uniqueClientName);

        // Act - Create client through DTO method
        clientDto.add(form);

        // Assert - Verify client was created by retrieving it from database
        ClientPojo dbClient = clientDao.getClientByName(uniqueClientName);
        assertNotNull(dbClient);
        assertEquals(uniqueClientName, dbClient.getName());
    }

    @Test
    public void testAddClientWithDuplicateName() throws ApiException {
        // Arrange
        String uniqueClientName = getUniqueClientName("DuplicateClient");
        ClientForm form1 = TestData.clientForm(uniqueClientName);
        ClientForm form2 = TestData.clientForm(uniqueClientName);

        // Act - Create first client through DTO method
        clientDto.add(form1);

        // Assert - Verify first client was created
        ClientPojo dbClient1 = clientDao.getClientByName(uniqueClientName);
        assertNotNull(dbClient1);
        assertEquals(uniqueClientName, dbClient1.getName());

        // Act & Assert - Try to create duplicate
        try {
            clientDto.add(form2);
            fail("Should throw ApiException for duplicate client name");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("Client") || e.getMessage().contains("already exists"));
        }

        // Assert - Verify only one client exists in database
        List<ClientPojo> allClients = clientDao.getAllPaged(0, 100);
        long duplicateCount = allClients.stream().filter(c -> c.getName().equals(uniqueClientName)).count();
        assertEquals(1, duplicateCount);
    }

    @Test
    public void testAddClientWithEmptyName() {
        // Arrange
        ClientForm form = TestData.clientForm("");

        // Act & Assert
        try {
            clientDto.add(form);
            fail("Should throw ApiException for empty client name");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("name") || e.getMessage().contains("blank"));
        }
    }

    @Test
    public void testAddClientWithNullName() {
        // Arrange
        ClientForm form = TestData.clientForm(null);

        // Act & Assert
        try {
            clientDto.add(form);
            fail("Should throw ApiException for null client name");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("name") || e.getMessage().contains("blank"));
        }
    }

    @Test
    public void testAddClientWithWhitespaceOnlyName() {
        // Arrange
        ClientForm form = TestData.clientForm("   ");

        // Act & Assert
        try {
            clientDto.add(form);
            fail("Should throw ApiException for whitespace-only client name");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("name") || e.getMessage().contains("blank"));
        }
    }

    @Test
    public void testAddClientWithTrimmedName() throws ApiException {
        // Arrange
        String baseName = getUniqueClientName("TrimmedClient");
        String nameWithSpaces = "  " + baseName + "  ";
        ClientForm form = TestData.clientForm(nameWithSpaces);

        // Act - Create client through DTO method
        clientDto.add(form);

        // Assert - Verify client was created with trimmed name
        ClientPojo dbClient = clientDao.getClientByName(baseName);
        assertNotNull(dbClient);
        assertEquals(baseName, dbClient.getName());
    }

    @Test
    public void testGetAllClients() throws ApiException {
        // Arrange - Create multiple clients using DTO methods
        String uniqueClientName1 = getUniqueClientName("GetAllClient1");
        String uniqueClientName2 = getUniqueClientName("GetAllClient2");
        ClientForm form1 = TestData.clientForm(uniqueClientName1);
        ClientForm form2 = TestData.clientForm(uniqueClientName2);
        
        clientDto.add(form1);
        clientDto.add(form2);

        // Act
        PaginatedResponse<ClientData> result = clientDto.getAll(0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertTrue(result.getTotalItems() >= 2);

        // Assert - Verify database state using DAO select method
        long dbCount = clientDao.countAll();
        assertTrue(dbCount >= 2);
        
        List<ClientPojo> dbClients = clientDao.getAllPaged(0, 10);
        assertTrue(dbClients.size() >= 2);
    }

    @Test
    public void testGetAllClientsWithPagination() throws ApiException {
        // Arrange - Create multiple clients
        for (int i = 0; i < 5; i++) {
            String uniqueClientName = getUniqueClientName("PaginationClient" + i);
            ClientForm form = TestData.clientForm(uniqueClientName);
            clientDto.add(form);
        }

        // Act - First page
        PaginatedResponse<ClientData> firstPage = clientDto.getAll(0, 2);
        
        // Act - Second page
        PaginatedResponse<ClientData> secondPage = clientDto.getAll(1, 2);

        // Assert
        assertNotNull(firstPage);
        assertNotNull(secondPage);
        assertEquals(2, firstPage.getContent().size());
        assertTrue(secondPage.getContent().size() >= 1);
        assertEquals(0, firstPage.getCurrentPage());
        assertEquals(1, secondPage.getCurrentPage());
        assertEquals(2, firstPage.getPageSize());
        assertEquals(2, secondPage.getPageSize());
        
        // Should not contain same clients
        assertNotEquals(firstPage.getContent().get(0).getId(), secondPage.getContent().get(0).getId());
    }

    @Test
    public void testSearchClients() throws ApiException {
        // Arrange - Create clients with searchable names
        String uniqueClientName1 = getUniqueClientName("SearchTestClient1");
        String uniqueClientName2 = getUniqueClientName("SearchTestClient2");
        String uniqueClientName3 = getUniqueClientName("DifferentClient");
        
        ClientForm form1 = TestData.clientForm(uniqueClientName1);
        ClientForm form2 = TestData.clientForm(uniqueClientName2);
        ClientForm form3 = TestData.clientForm(uniqueClientName3);
        
        clientDto.add(form1);
        clientDto.add(form2);
        clientDto.add(form3);

        // Act
        PaginatedResponse<ClientData> result = clientDto.searchClients("SearchTest", 0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
        for (ClientData client : result.getContent()) {
            assertTrue(client.getName().toLowerCase().contains("searchtest"));
        }

        // Assert - Verify database state using DAO select method
        long dbCount = clientDao.countByQuery("SearchTest");
        assertTrue(dbCount >= 2);
        
        List<ClientPojo> dbClients = clientDao.searchByQuery("SearchTest", 0, 10);
        assertTrue(dbClients.size() >= 2);
    }

    @Test
    public void testSearchClientsNoResults() throws ApiException {
        // Act
        PaginatedResponse<ClientData> result = clientDto.searchClients("NonExistentClient", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalItems());
    }

    @Test
    public void testGetClientByNameUsingSearch() throws ApiException {
        // Arrange
        String uniqueClientName = getUniqueClientName("GetByNameClient");
        ClientForm form = TestData.clientForm(uniqueClientName);
        
        clientDto.add(form);

        // Act - Use search to find exact client name
        PaginatedResponse<ClientData> result = clientDto.searchClients(uniqueClientName, 0, 10);

        // Assert - Verify DTO method results
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        
        // Find our client in the search results
        ClientData foundClient = result.getContent().stream()
            .filter(c -> c.getName().equals(uniqueClientName))
            .findFirst()
            .orElse(null);
        
        assertNotNull(foundClient);
        assertEquals(uniqueClientName, foundClient.getName());

        // Assert - Verify database state using DAO select method
        ClientPojo dbClient = clientDao.getClientByName(uniqueClientName);
        assertNotNull(dbClient);
        assertEquals(uniqueClientName, dbClient.getName());
        assertEquals(foundClient.getId(), dbClient.getId());
    }

    @Test
    public void testAddMultipleClientsAndVerifyOrder() throws ApiException {
        // Arrange - Create multiple clients
        String[] clientNames = new String[3];
        for (int i = 0; i < 3; i++) {
            clientNames[i] = getUniqueClientName("OrderClient" + i);
            ClientForm form = TestData.clientForm(clientNames[i]);
            clientDto.add(form);
        }

        // Act
        PaginatedResponse<ClientData> result = clientDto.getAll(0, 10);

        // Assert - Verify clients are returned in sorted order
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 3);
        
        // Find our test clients in the result
        List<ClientData> testClients = result.getContent().stream()
                .filter(c -> c.getName().contains("OrderClient"))
                .collect(java.util.stream.Collectors.toList());
        
        assertTrue(testClients.size() >= 3);
        
        // Verify they are in alphabetical order
        for (int i = 0; i < testClients.size() - 1; i++) {
            assertTrue(testClients.get(i).getName().compareTo(testClients.get(i + 1).getName()) <= 0);
        }
    }

    @Test
    public void testSearchClientsPartialMatch() throws ApiException {
        // Arrange
        String uniqueClientName = getUniqueClientName("PartialMatchTestClient");
        ClientForm form = TestData.clientForm(uniqueClientName);
        
        clientDto.add(form);

        // Act - Search with partial name
        PaginatedResponse<ClientData> result = clientDto.searchClients("PartialMatch", 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        
        boolean found = false;
        for (ClientData client : result.getContent()) {
            if (client.getName().equals(uniqueClientName)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
} 