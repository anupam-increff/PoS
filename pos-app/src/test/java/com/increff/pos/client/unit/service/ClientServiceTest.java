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
public class ClientServiceTest {

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

    // Creation Tests
    @Test
    public void testAddClientWithValidData() throws ApiException {
        when(clientDao.getClientByName("Test Client")).thenReturn(null);
        clientService.addClient(testClient);
        verify(clientDao).insert(testClient);
    }

    @Test(expected = ApiException.class)
    public void testAddClientWithDuplicateName() throws ApiException {
        when(clientDao.getClientByName("Test Client")).thenReturn(new ClientPojo());
        clientService.addClient(testClient);
    }

    @Test(expected = ApiException.class)
    public void testAddClientWithNullName() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName(null);
        clientService.addClient(client);
    }

    @Test(expected = ApiException.class)
    public void testAddClientWithEmptyName() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName("");
        clientService.addClient(client);
    }

    @Test(expected = ApiException.class)
    public void testAddClientWithWhitespaceName() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName("   ");
        clientService.addClient(client);
    }

    @Test(expected = ApiException.class)
    public void testAddClientWithNameExceedingMaxLength() throws ApiException {
        ClientPojo client = new ClientPojo();
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("A");
        }
        client.setName(longName.toString());
        clientService.addClient(client);
    }

    // Update Tests
    @Test
    public void testUpdateClientWithValidData() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setId(1);
        client.setName("Old Name");

        when(clientDao.getById(1)).thenReturn(client);
        when(clientDao.getClientByName("New Name")).thenReturn(null);

        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("New Name");

        clientService.update(1, updatePojo);

        assertEquals("New Name", client.getName());
        verify(clientDao).getById(1);
        verify(clientDao, times(2)).getClientByName("New Name");
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientRejectsDuplicateName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("New Name");

        when(clientDao.getClientByName("New Name")).thenReturn(new ClientPojo());

        clientService.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientRejectsNonExistentId() throws ApiException {
        when(clientDao.getById(1)).thenReturn(null);
        
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("New Name");
        
        clientService.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientRejectsNullName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName(null);

        clientService.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientRejectsEmptyName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("");

        clientService.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientRejectsWhitespaceName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("   ");

        clientService.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientRejectsNameExceedingMaxLength() throws ApiException {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("A");
        }

        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName(longName.toString());

        clientService.update(1, updatePojo);
    }

    // Search Tests
    @Test
    public void testGetClientByValidId() throws ApiException {
        when(clientDao.getById(1)).thenReturn(testClient);
        ClientPojo result = clientService.getCheckClientById(1);
        assertEquals(testClient, result);
    }

    @Test(expected = ApiException.class)
    public void testGetClientByNonExistentId() throws ApiException {
        when(clientDao.getById(1)).thenReturn(null);
        clientService.getCheckClientById(1);
    }

    @Test
    public void testGetClientByValidName() throws ApiException {
        when(clientDao.getClientByName("Test Client")).thenReturn(testClient);
        ClientPojo result = clientService.getCheckClientByName("Test Client");
        assertEquals(testClient, result);
    }

    @Test(expected = ApiException.class)
    public void testGetClientByNonExistentName() throws ApiException {
        when(clientDao.getClientByName("Invalid")).thenReturn(null);
        clientService.getCheckClientByName("Invalid");
    }

    @Test
    public void testGetAllClientsWithPagination() {
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.getAllClients(0, 10)).thenReturn(clients);
        List<ClientPojo> result = clientService.getAllClients(0, 10);
        assertEquals(clients, result);
    }

    @Test
    public void testGetAllClientsReturnsEmptyList() {
        when(clientDao.getAllClients(0, 10)).thenReturn(Collections.emptyList());
        List<ClientPojo> result = clientService.getAllClients(0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCountTotalClients() {
        when(clientDao.countAll()).thenReturn(5L);
        Long count = clientService.countAll();
        assertEquals(Long.valueOf(5), count);
    }

    @Test
    public void testSearchClientsByNameWithResults() {
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.searchClientByName("Test", 0, 10)).thenReturn(clients);
        List<ClientPojo> result = clientService.searchClients("Test", 0, 10);
        assertEquals(clients, result);
    }

    @Test
    public void testSearchClientsByNameReturnsEmptyList() {
        when(clientDao.searchClientByName("NonExistent", 0, 10)).thenReturn(Collections.emptyList());
        List<ClientPojo> result = clientService.searchClients("NonExistent", 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCountClientsByNameWithResults() {
        when(clientDao.countByQuery("Test")).thenReturn(2L);
        Long count = clientService.countByQuery("Test");
        assertEquals(Long.valueOf(2), count);
    }

    @Test
    public void testCountClientsByNameReturnsZero() {
        when(clientDao.countByQuery("NonExistent")).thenReturn(0L);
        Long count = clientService.countByQuery("NonExistent");
        assertEquals(Long.valueOf(0), count);
    }
} 