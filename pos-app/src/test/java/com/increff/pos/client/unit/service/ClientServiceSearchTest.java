package com.increff.pos.client.unit.service;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceSearchTest {

    @Mock
    private ClientDao clientDao;

    @InjectMocks
    private ClientService clientService;

    private ClientPojo testClient;

    @Before
    public void setUp() {
        testClient = new ClientPojo();
        testClient.setId(1);
        testClient.setName("Test Client");
    }

    // Test getting client by valid ID
    @Test
    public void testGetCheckClientById() throws ApiException {
        when(clientDao.getById(1)).thenReturn(testClient);
        ClientPojo result = clientService.getCheckClientById(1);
        assertEquals(testClient, result);
    }

    // Test getting client by invalid ID
    @Test(expected = ApiException.class)
    public void testGetCheckClientByIdInvalid() throws ApiException {
        when(clientDao.getById(1)).thenReturn(null);
        clientService.getCheckClientById(1);
    }

    // Test getting client by valid name
    @Test
    public void testGetCheckClientByName() throws ApiException {
        when(clientDao.getClientByName("Test Client")).thenReturn(testClient);
        ClientPojo result = clientService.getCheckClientByName("Test Client");
        assertEquals(testClient, result);
    }

    // Test getting client by invalid name
    @Test(expected = ApiException.class)
    public void testGetCheckClientByNameInvalid() throws ApiException {
        when(clientDao.getClientByName("Invalid")).thenReturn(null);
        clientService.getCheckClientByName("Invalid");
    }

    // Test getting all clients with pagination
    @Test
    public void testGetAllClients() {
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.getAllClients(0, 10)).thenReturn(clients);
        List<ClientPojo> result = clientService.getAllClients(0, 10);
        assertEquals(clients, result);
    }

    // Test getting all clients when empty
    @Test
    public void testGetAllClientsEmpty() {
        when(clientDao.getAllClients(0, 10)).thenReturn(Collections.emptyList());
        List<ClientPojo> result = clientService.getAllClients(0, 10);
        assertTrue(result.isEmpty());
    }

    // Test counting all clients
    @Test
    public void testCountAll() {
        when(clientDao.countAll()).thenReturn(5L);
        Long count = clientService.countAll();
        assertEquals(Long.valueOf(5), count);
    }

    // Test searching clients
    @Test
    public void testSearchClients() {
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.searchClientByName("Test", 0, 10)).thenReturn(clients);
        List<ClientPojo> result = clientService.searchClients("Test", 0, 10);
        assertEquals(clients, result);
    }

    // Test searching with empty results
    @Test
    public void testSearchClientsNoResults() {
        when(clientDao.searchClientByName("NonExistent", 0, 10)).thenReturn(Collections.emptyList());
        List<ClientPojo> result = clientService.searchClients("NonExistent", 0, 10);
        assertTrue(result.isEmpty());
    }

    // Test counting search results
    @Test
    public void testCountSearchResults() {
        when(clientDao.countByQuery("Test")).thenReturn(2L);
        Long count = clientService.countByQuery("Test");
        assertEquals(Long.valueOf(2), count);
    }

    // Test counting search results with no matches
    @Test
    public void testCountSearchResultsNoMatches() {
        when(clientDao.countByQuery("NonExistent")).thenReturn(0L);
        Long count = clientService.countByQuery("NonExistent");
        assertEquals(Long.valueOf(0), count);
    }
} 