package com.increff.pos.client.unit.dto;

import com.increff.pos.setup.TestData;
import com.increff.pos.dto.ClientDto;
import com.increff.pos.service.ClientService;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
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
public class ClientDtoTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientDto clientDto;

    private ClientPojo testClient;
    private ClientForm testClientForm;

    @Before
    public void setUp() {
        testClient = TestData.client(1);
        testClient.setName("TestClient");
        
        testClientForm = TestData.clientForm("TestClient");
    }

    @Test
    public void testAdd_Success() {
        // Given
        doNothing().when(clientService).addClient(any(ClientPojo.class));

        // When
        clientDto.add(testClientForm);

        // Then
        verify(clientService, times(1)).addClient(any(ClientPojo.class));
    }

    @Test
    public void testGetAll_Success() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientService.getAllClients(0, 10)).thenReturn(clients);
        when(clientService.countAll()).thenReturn(1L);

        // When
        PaginatedResponse<ClientData> result = clientDto.getAll(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TestClient", result.getContent().get(0).getName());
        verify(clientService, times(1)).getAllClients(0, 10);
        verify(clientService, times(1)).countAll();
    }

    @Test
    public void testSearchClients_Success() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientService.searchClients("Test", 0, 10)).thenReturn(clients);
        when(clientService.countByQuery("Test")).thenReturn(1L);

        // When
        PaginatedResponse<ClientData> result = clientDto.searchClients("Test", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TestClient", result.getContent().get(0).getName());
        verify(clientService, times(1)).searchClients("Test", 0, 10);
        verify(clientService, times(1)).countByQuery("Test");
    }
} 