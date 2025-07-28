package com.increff.pos.client.unit.dao;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.setup.AbstractTest;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ClientDaoTest extends AbstractTest {

    @Autowired
    private ClientDao clientDao;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.clientWithoutId("Test Client DAO");
    }

    /**
     * Tests inserting a client and retrieving it by name.
     * Verifies basic DAO insert and select operations.
     */
    @Test
    public void testInsertAndGetClientByName() {
        // When
        clientDao.insert(testClient);

        // Then
        ClientPojo retrieved = clientDao.getClientByName("Test Client DAO");
        assertNotNull("Client should be found by name", retrieved);
        assertEquals("Client name should match", "Test Client DAO", retrieved.getName());
    }

    /**
     * Tests retrieving client by non-existent name.
     * Verifies proper handling when no client matches the name.
     */
    @Test
    public void testGetClientByNameNotFound() {
        // When
        ClientPojo result = clientDao.getClientByName("Non-Existent Client");

        // Then
        assertNull("Non-existent client should return null", result);
    }

    /**
     * Tests counting all clients in the database.
     * Verifies the countAll method returns accurate count.
     */
    @Test
    public void testCountAll() {
        // Given
        long initialCount = clientDao.countAll();
        clientDao.insert(testClient);

        // When
        long finalCount = clientDao.countAll();

        // Then
        assertEquals("Count should increase by one", initialCount + 1, finalCount);
    }

    /**
     * Tests counting clients by search query.
     * Verifies the search count functionality.
     */
    @Test
    public void testCountByQuery() {
        // Given
        clientDao.insert(testClient);

        // When
        long count = clientDao.countByQuery("Test");

        // Then
        assertTrue("Should find at least one matching client", count >= 1);
    }

    /**
     * Tests retrieving all clients.
     * Verifies the getAll method returns all persisted clients.
     */
    @Test
    public void testGetAllClients() {
        // Given
        clientDao.insert(testClient);

        // When
        List<ClientPojo> allClients = clientDao.getAll();

        // Then
        assertNotNull("Client list should not be null", allClients);
        assertTrue("Should contain at least one client", allClients.size() >= 1);
    }

    /**
     * Tests searching clients by name pattern.
     * Verifies partial matching and search functionality.
     */
    @Test
    public void testSearchClientByName() {
        // Given
        clientDao.insert(testClient);

        // When
        List<ClientPojo> searchResults = clientDao.searchClientByName("Test", 0, 10);

        // Then
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("Should find one matching client", 1, searchResults.size());
        assertEquals("Found client should match", "Test Client DAO", searchResults.get(0).getName());
    }
} 