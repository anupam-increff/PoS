package com.increff.pos.client.unit.service;

import com.increff.pos.setup.TestData;
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
        testClient = TestData.client(1);
        testClient.setName("Test Client");
    }

    @Test
    public void testAddClient_Success() {
        // Given
        when(clientDao.getClientByName("Test Client")).thenReturn(null);
        doNothing().when(clientDao).insert(any(ClientPojo.class));

        // When
        clientService.addClient(testClient);

        // Then
        verify(clientDao, times(1)).getClientByName("Test Client");
        verify(clientDao, times(1)).insert(testClient);
    }

    @Test(expected = ApiException.class)
    public void testAddClient_DuplicateName() {
        // Given
        when(clientDao.getClientByName("Test Client")).thenReturn(testClient);

        // When
        clientService.addClient(testClient);

        // Then - exception should be thrown
    }

    @Test
    public void testGetAllClients_Success() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.getAllClients(0, 10)).thenReturn(clients);

        // When
        List<ClientPojo> result = clientService.getAllClients(0, 10);

        // Then
        assertEquals(clients, result);
        verify(clientDao, times(1)).getAllClients(0, 10);
    }

    @Test
    public void testCountAll_Success() {
        // Given
        when(clientDao.countAll()).thenReturn(5L);

        // When
        long count = clientService.countAll();

        // Then
        assertEquals(5L, count);
        verify(clientDao, times(1)).countAll();
    }

    @Test
    public void testSearchClients_Success() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientDao.searchClientByName("Test", 0, 10)).thenReturn(clients);

        // When
        List<ClientPojo> result = clientService.searchClients("Test", 0, 10);

        // Then
        assertEquals(clients, result);
        verify(clientDao, times(1)).searchClientByName("Test", 0, 10);
    }

    @Test
    public void testCountByQuery_Success() {
        // Given
        when(clientDao.countByQuery("Test")).thenReturn(3L);

        // When
        long count = clientService.countByQuery("Test");

        // Then
        assertEquals(3L, count);
        verify(clientDao, times(1)).countByQuery("Test");
    }
} 