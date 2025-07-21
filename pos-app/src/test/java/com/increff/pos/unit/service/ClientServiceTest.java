package com.increff.pos.unit.service;

import com.increff.pos.exception.ApiException;
import com.increff.pos.service.ClientService;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

    @Mock
    private ClientService clientService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testClientNameValidation() {
        // Test null name
        ClientPojo clientWithNull = new ClientPojo();
        clientWithNull.setName(null);
        
        try {
            clientService.addClient(clientWithNull);
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
        
        // Test empty name
        ClientPojo clientWithEmpty = new ClientPojo();
        clientWithEmpty.setName("");
        
        try {
            clientService.addClient(clientWithEmpty);
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
        
        // Test whitespace name
        ClientPojo clientWithWhitespace = new ClientPojo();
        clientWithWhitespace.setName("   ");
        
        try {
            clientService.addClient(clientWithWhitespace);
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
    }

    @Test
    public void testClientServiceGetAll() {
        // Arrange
        List<ClientPojo> mockClients = new ArrayList<>();
        ClientPojo client1 = new ClientPojo();
        client1.setId(1);
        client1.setName("Client 1");
        mockClients.add(client1);
        
        ClientPojo client2 = new ClientPojo();
        client2.setId(2);
        client2.setName("Client 2");
        mockClients.add(client2);
        
        when(clientService.getAllClients(0, 10)).thenReturn(mockClients);
        
        // Act
        List<ClientPojo> result = clientService.getAllClients(0, 10);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Client 1", result.get(0).getName());
        assertEquals("Client 2", result.get(1).getName());
        verify(clientService, times(1)).getAllClients(0, 10);
    }

    @Test
    public void testClientServiceCount() {
        // Arrange
        when(clientService.countAll()).thenReturn(5L);
        
        // Act
        long count = clientService.countAll();
        
        // Assert
        assertEquals(5L, count);
        verify(clientService, times(1)).countAll();
    }

    @Test
    public void testClientServiceSearch() {
        // Arrange
        List<ClientPojo> mockClients = new ArrayList<>();
        ClientPojo client = new ClientPojo();
        client.setId(1);
        client.setName("Test Client");
        mockClients.add(client);
        
        when(clientService.searchClients("Test", 0, 10)).thenReturn(mockClients);
        when(clientService.countByQuery("Test")).thenReturn(1L);
        
        // Act
        List<ClientPojo> result = clientService.searchClients("Test", 0, 10);
        long count = clientService.countByQuery("Test");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Client", result.get(0).getName());
        assertEquals(1L, count);
        verify(clientService, times(1)).searchClients("Test", 0, 10);
        verify(clientService, times(1)).countByQuery("Test");
    }

    @Test
    public void testClientServiceGetByName() {
        // Arrange
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName("Test Client");
        
        when(clientService.getCheckClientByName("Test Client")).thenReturn(mockClient);
        
        // Act
        ClientPojo result = clientService.getCheckClientByName("Test Client");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId().intValue());
        assertEquals("Test Client", result.getName());
        verify(clientService, times(1)).getCheckClientByName("Test Client");
    }

    @Test
    public void testClientServiceGetById() {
        // Arrange
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName("Test Client");
        
        when(clientService.getCheckClientById(1)).thenReturn(mockClient);
        
        // Act
        ClientPojo result = clientService.getCheckClientById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId().intValue());
        assertEquals("Test Client", result.getName());
        verify(clientService, times(1)).getCheckClientById(1);
    }

    @Test
    public void testClientServiceGetByNameNotFound() {
        // Arrange
        when(clientService.getCheckClientByName("NonExistent"))
            .thenThrow(new ApiException("Client with name NonExistent does not exist"));
        
        // Act & Assert
        try {
            clientService.getCheckClientByName("NonExistent");
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
        verify(clientService, times(1)).getCheckClientByName("NonExistent");
    }

    @Test
    public void testClientServiceGetByIdNotFound() {
        // Arrange
        when(clientService.getCheckClientById(999))
            .thenThrow(new ApiException("Client with id 999 does not exist"));
        
        // Act & Assert
        try {
            clientService.getCheckClientById(999);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
        verify(clientService, times(1)).getCheckClientById(999);
    }

    @Test
    public void testClientServiceAddDuplicate() {
        // Arrange
        ClientPojo client = new ClientPojo();
        client.setName("Existing Client");
        
        doThrow(new ApiException("Client with name already exists"))
            .when(clientService).addClient(client);
        
        // Act & Assert
        try {
            clientService.addClient(client);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("already exists"));
        }
        verify(clientService, times(1)).addClient(client);
    }

    @Test
    public void testClientServiceUpdateSuccess() {
        // Arrange
        ClientPojo client = new ClientPojo();
        client.setName("Updated Client");
        
        doNothing().when(clientService).update(1, client);
        
        // Act
        clientService.update(1, client);
        
        // Assert
        verify(clientService, times(1)).update(1, client);
    }

    @Test
    public void testClientServiceUpdateNotFound() {
        // Arrange
        ClientPojo client = new ClientPojo();
        client.setName("Updated Client");
        
        doThrow(new ApiException("Client with id 999 does not exist"))
            .when(clientService).update(999, client);
        
        // Act & Assert
        try {
            clientService.update(999, client);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
        verify(clientService, times(1)).update(999, client);
    }

    @Test
    public void testPaginationLimits() {
        // Test with large page size
        when(clientService.getAllClients(0, 1000)).thenReturn(new ArrayList<>());
        
        List<ClientPojo> result = clientService.getAllClients(0, 1000);
        assertNotNull(result);
        
        // Test with zero page size
        when(clientService.getAllClients(0, 0)).thenReturn(new ArrayList<>());
        
        List<ClientPojo> result2 = clientService.getAllClients(0, 0);
        assertNotNull(result2);
        
        verify(clientService, times(1)).getAllClients(0, 1000);
        verify(clientService, times(1)).getAllClients(0, 0);
    }

    @Test
    public void testSearchWithSpecialCharacters() {
        // Arrange
        List<ClientPojo> mockClients = new ArrayList<>();
        when(clientService.searchClients("Test@#$", 0, 10)).thenReturn(mockClients);
        when(clientService.countByQuery("Test@#$")).thenReturn(0L);
        
        // Act
        List<ClientPojo> result = clientService.searchClients("Test@#$", 0, 10);
        long count = clientService.countByQuery("Test@#$");
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(0L, count);
        verify(clientService, times(1)).searchClients("Test@#$", 0, 10);
        verify(clientService, times(1)).countByQuery("Test@#$");
    }

    @Test
    public void testSearchWithEmptyQuery() {
        // Arrange
        List<ClientPojo> mockClients = new ArrayList<>();
        when(clientService.searchClients("", 0, 10)).thenReturn(mockClients);
        when(clientService.countByQuery("")).thenReturn(0L);
        
        // Act
        List<ClientPojo> result = clientService.searchClients("", 0, 10);
        long count = clientService.countByQuery("");
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(0L, count);
        verify(clientService, times(1)).searchClients("", 0, 10);
        verify(clientService, times(1)).countByQuery("");
    }
} 