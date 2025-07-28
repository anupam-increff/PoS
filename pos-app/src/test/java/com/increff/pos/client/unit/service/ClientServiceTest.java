package com.increff.pos.client.unit.service;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

    @Mock
    private ClientDao clientDao;

    @InjectMocks
    private ClientService clientService;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = TestData.client(1);
        testClient.setName("Test Client");
    }

    /**
     * Tests successfully adding a new client.
     * Verifies proper validation and database insertion.
     */
    @Test
    public void testAddClient() {
        // Given
        when(clientDao.getClientByName("Test Client")).thenReturn(null);
        doNothing().when(clientDao).insert(any(ClientPojo.class));

        // When
        clientService.addClient(testClient);

        // Then
        verify(clientDao, times(1)).getClientByName("Test Client");
        verify(clientDao, times(1)).insert(testClient);
    }

    /**
     * Tests adding a client with duplicate name.
     * Verifies proper exception handling for name conflicts.
     */
    @Test(expected = ApiException.class)
    public void testAddClientDuplicateName() {
        // Given
        when(clientDao.getClientByName("Test Client")).thenReturn(testClient);

        // When
        clientService.addClient(testClient);

        // Then - exception should be thrown
    }

    /**
     * Tests retrieving all clients with pagination.
     * Verifies proper delegation to DAO layer.
     */
    @Test
    public void testGetAllClients() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.getAllClients(0, 10)).thenReturn(clients);

        // When
        List<ClientPojo> result = clientService.getAllClients(0, 10);

        // Then
        assertEquals("Should return client list from DAO", clients, result);
        verify(clientDao, times(1)).getAllClients(0, 10);
    }

    /**
     * Tests searching clients by query string.
     * Verifies proper search delegation and result handling.
     */
    @Test
    public void testSearchClients() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.searchClientByName("Test", 0, 10)).thenReturn(clients);

        // When
        List<ClientPojo> result = clientService.searchClients("Test", 0, 10);

        // Then
        assertEquals("Should return search results from DAO", clients, result);
        verify(clientDao, times(1)).searchClientByName("Test", 0, 10);
    }

    /**
     * Tests retrieving client by valid ID.
     * Verifies proper client lookup and validation.
     */
    @Test
    public void testGetCheckClientById() {
        // Given
        when(clientDao.getById(1)).thenReturn(testClient);

        // When
        ClientPojo result = clientService.getCheckClientById(1);

        // Then
        assertEquals("Should return client from DAO", testClient, result);
        verify(clientDao, times(1)).getById(1);
    }

    /**
     * Tests retrieving client by invalid ID.
     * Verifies proper exception handling for non-existent clients.
     */
    @Test(expected = ApiException.class)
    public void testGetCheckClientByIdNotFound() {
        // Given
        when(clientDao.getById(999)).thenReturn(null);

        // When
        clientService.getCheckClientById(999);

        // Then - exception should be thrown
    }
} 