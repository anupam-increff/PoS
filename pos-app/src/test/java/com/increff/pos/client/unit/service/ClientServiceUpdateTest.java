package com.increff.pos.client.unit.service;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceUpdateTest {

    @InjectMocks
    private ClientService service;

    @Mock
    private ClientDao dao;

    @Test
    public void testUpdateClientDuplicateName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("New Name");

        when(dao.getClientByName("New Name")).thenReturn(new ClientPojo());

        try {
            service.update(1, updatePojo);
        } catch (ApiException e) {
            assertEquals("Client with name already exists", e.getMessage());
        }
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonExistentClient() throws ApiException {
        when(dao.getById(1)).thenReturn(null);
        
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("New Name");
        
        service.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientNullName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName(null);

        service.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientEmptyName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("");

        service.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientWhitespaceName() throws ApiException {
        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("   ");

        service.update(1, updatePojo);
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientNameTooLong() throws ApiException {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("A");
        }

        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName(longName.toString());

        service.update(1, updatePojo);
    }

    @Test
    public void testUpdateClientSuccess() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setId(1);
        client.setName("Old Name");

        when(dao.getById(1)).thenReturn(client);
        when(dao.getClientByName("New Name")).thenReturn(null);

        ClientPojo updatePojo = new ClientPojo();
        updatePojo.setName("New Name");

        service.update(1, updatePojo);

        assertEquals("New Name", client.getName());
        verify(dao).getById(1);
        verify(dao, times(2)).getClientByName("New Name");
    }
} 