package com.increff.pos.client.unit.dao;

import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * DAO unit tests for ClientDao.
 * Tests direct database operations for Client entity.
 */
public class ClientDaoTest extends AbstractTest {

    @Autowired
    private ClientDao clientDao;

    @Test
    public void testInsertAndGetClientByName_Success() {
        // Given
        ClientPojo client = TestData.clientWithoutId("Test Client DAO");

        // When
        clientDao.insert(client);
        ClientPojo savedClient = clientDao.getClientByName("Test Client DAO");

        // Then
        assertNotNull("Client should be saved and retrieved", savedClient);
        assertEquals("Test Client DAO", savedClient.getName());
        assertNotNull("ID should be generated", savedClient.getId());
    }

    @Test
    public void testGetClientByName_NotFound() {
        // When
        ClientPojo client = clientDao.getClientByName("NonExistent Client");

        // Then
        assertNull("Non-existent client should return null", client);
    }

    @Test
    public void testGetAllClients_Success() {
        // Given
        clientDao.insert(TestData.clientWithoutId("Client 1"));
        clientDao.insert(TestData.clientWithoutId("Client 2"));

        // When
        List<ClientPojo> clients = clientDao.getAllClients(0, 10);

        // Then
        assertNotNull("Clients list should not be null", clients);
        assertEquals(2, clients.size());
    }

    @Test
    public void testSearchClientByName_Success() {
        // Given
        clientDao.insert(TestData.clientWithoutId("Search Client Alpha"));
        clientDao.insert(TestData.clientWithoutId("Search Client Beta"));
        clientDao.insert(TestData.clientWithoutId("Different Name"));

        // When
        List<ClientPojo> results = clientDao.searchClientByName("Search", 0, 10);

        // Then
        assertNotNull("Search results should not be null", results);
        assertEquals(2, results.size());
        assertTrue("All results should contain 'Search'",
            results.stream().allMatch(c -> c.getName().contains("Search")));
    }

    @Test
    public void testCountByQuery_Success() {
        // Given
        clientDao.insert(TestData.clientWithoutId("Query Client 1"));
        clientDao.insert(TestData.clientWithoutId("Query Client 2"));
        clientDao.insert(TestData.clientWithoutId("Other Name"));

        // When
        long count = clientDao.countByQuery("Query");

        // Then
        assertEquals(2L, count);
    }

    @Test
    public void testCountAll_Success() {
        // Given
        clientDao.insert(TestData.clientWithoutId("Client A"));
        clientDao.insert(TestData.clientWithoutId("Client B"));
        clientDao.insert(TestData.clientWithoutId("Client C"));

        // When
        long count = clientDao.countAll();

        // Then
        assertEquals(3L, count);
    }
} 