package com.increff.pos.client.unit.dto;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientDtoTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientDto clientDto;

    private Validator validator;
    private ClientForm testClientForm;
    private ClientPojo testClient;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        testClientForm = new ClientForm();
        testClientForm.setName("Test Client");
        
        testClient = TestData.client(1);
        testClient.setName("Test Client");
    }

    /**
     * Tests adding a client with valid data.
     * Verifies proper service delegation and processing.
     */
    @Test
    public void testAdd() {
        // Given
        doNothing().when(clientService).addClient(any(ClientPojo.class));

        // When
        clientDto.add(testClientForm);

        // Then
        verify(clientService, times(1)).addClient(any(ClientPojo.class));
    }

    /**
     * Tests adding client with null name.
     * Verifies proper validation handling.
     */
    @Test
    public void testAddClientNullName() {
        // Given
        testClientForm.setName(null);

        // When
        Set<ConstraintViolation<ClientForm>> violations = validator.validate(testClientForm);

        // Then
        assertFalse("Should have validation violations for null name", violations.isEmpty());
    }

    /**
     * Tests adding client with empty name.
     * Verifies proper validation for empty strings.
     */
    @Test
    public void testAddClientEmptyName() {
        // Given
        testClientForm.setName("");

        // When
        Set<ConstraintViolation<ClientForm>> violations = validator.validate(testClientForm);

        // Then
        assertFalse("Should have validation violations for empty name", violations.isEmpty());
    }

    /**
     * Tests adding client with name containing only spaces.
     * Verifies proper validation for whitespace-only input.
     */
    @Test
    public void testAddClientWhitespaceName() {
        // Given
        testClientForm.setName("   ");

        // When
        Set<ConstraintViolation<ClientForm>> violations = validator.validate(testClientForm);

        // Then
        assertFalse("Should have validation violations for whitespace-only name", violations.isEmpty());
    }

    /**
     * Tests retrieving all clients with pagination.
     * Verifies proper service delegation and response handling.
     */
    @Test
    public void testGetAll() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientService.getAllClients(0, 10)).thenReturn(clients);
        when(clientService.countAll()).thenReturn(1L);

        // When
        PaginatedResponse<ClientData> result = clientDto.getAll(0, 10);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should contain one client", 1, result.getContent().size());
        assertEquals("Client name should match", "Test Client", result.getContent().get(0).getName());
        verify(clientService, times(1)).getAllClients(0, 10);
        verify(clientService, times(1)).countAll();
    }

    /**
     * Tests searching clients by name with pagination.
     * Verifies proper search functionality delegation.
     */
    @Test
    public void testSearchClients() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientService.searchClients("Test", 0, 10)).thenReturn(clients);
        when(clientService.countByQuery("Test")).thenReturn(1L);

        // When
        PaginatedResponse<ClientData> result = clientDto.searchClients("Test", 0, 10);

        // Then
        assertNotNull("Search results should not be null", result);
        assertEquals("Should find one matching client", 1, result.getContent().size());
        assertEquals("Found client should match", "Test Client", result.getContent().get(0).getName());
        verify(clientService, times(1)).searchClients("Test", 0, 10);
        verify(clientService, times(1)).countByQuery("Test");
    }

    /**
     * Tests searching with case sensitivity.
     * Verifies case handling in search functionality.
     */
    @Test
    public void testSearchClientsCaseSensitivity() {
        // Given
        List<ClientPojo> clients = Arrays.asList(testClient);
        when(clientService.searchClients("test", 0, 10)).thenReturn(clients);
        when(clientService.countByQuery("test")).thenReturn(1L);

        // When
        PaginatedResponse<ClientData> result = clientDto.searchClients("test", 0, 10);

        // Then
        assertNotNull("Should handle case properly", result);
        verify(clientService, times(1)).searchClients("test", 0, 10);
        verify(clientService, times(1)).countByQuery("test");
    }

    /**
     * Tests searching with null search term.
     * Verifies proper handling of null search parameters.
     */
    @Test
    public void testSearchClientsNull() {
        // Given
        List<ClientPojo> clients = Arrays.asList();
        when(clientService.searchClients(null, 0, 10)).thenReturn(clients);
        when(clientService.countByQuery(null)).thenReturn(0L);

        // When
        PaginatedResponse<ClientData> result = clientDto.searchClients(null, 0, 10);

        // Then
        assertNotNull("Should handle null search term", result);
        assertEquals("Should return empty results", 0, result.getContent().size());
        verify(clientService, times(1)).searchClients(null, 0, 10);
        verify(clientService, times(1)).countByQuery(null);
    }

    /**
     * Tests updating client with valid data.
     * Verifies proper service delegation for updates.
     */
    @Test
    public void testUpdate() {
        // Given
        doNothing().when(clientService).update(eq(1), any(ClientPojo.class));

        // When
        clientDto.update(1, testClientForm);

        // Then
        verify(clientService, times(1)).update(eq(1), any(ClientPojo.class));
    }
} 