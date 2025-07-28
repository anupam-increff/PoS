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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceCreationTest {

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

    // Test successful client creation
    @Test
    public void testCreateClient() throws ApiException {
        when(clientDao.getClientByName("Test Client")).thenReturn(null);
        clientService.addClient(testClient);
        verify(clientDao).insert(testClient);
    }

    // Test creating duplicate client
    @Test(expected = ApiException.class)
    public void testCreateDuplicateClient() throws ApiException {
        when(clientDao.getClientByName("Test Client")).thenReturn(new ClientPojo());
        clientService.addClient(testClient);
    }

    // Test validation - null name
    @Test(expected = ApiException.class)
    public void testCreateClientNullName() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName(null);
        clientService.addClient(client);
    }

    // Test validation - empty name
    @Test(expected = ApiException.class)
    public void testCreateClientEmptyName() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName("");
        clientService.addClient(client);
    }

    // Test validation - whitespace name
    @Test(expected = ApiException.class)
    public void testCreateClientWhitespaceName() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName("   ");
        clientService.addClient(client);
    }

    // Test validation - name too long
    @Test(expected = ApiException.class)
    public void testCreateClientNameTooLong() throws ApiException {
        ClientPojo client = new ClientPojo();
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("A");
        }
        client.setName(longName.toString());
        clientService.addClient(client);
    }
} 