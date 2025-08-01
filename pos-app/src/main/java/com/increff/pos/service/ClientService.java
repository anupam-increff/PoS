package com.increff.pos.service;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = ApiException.class)
public class ClientService {

    @Autowired
    private ClientDao clientDao;

    public void addClient(ClientPojo clientPojo) {
        validateClientName(clientPojo);
        validateDuplicateClientWithSameName(clientPojo);
        clientDao.insert(clientPojo);
    }

    public List<ClientPojo> getAllClients(Integer page, Integer pageSize) {
        return clientDao.getAllClients(page, pageSize);
    }

    public Long countAll() {
        return clientDao.countAll();
    }

    public List<ClientPojo> searchClients(String query, Integer page, Integer pageSize) {
        return clientDao.searchClientByName(query, page, pageSize);
    }

    public Long countByQuery(String query) {
        return clientDao.countByQuery(query);
    }

    public ClientPojo getCheckClientByName(String clientName) {
        ClientPojo client = clientDao.getClientByName(clientName);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with name " + clientName + " does not exist");
        }
        return client;
    }

    public ClientPojo getCheckClientById(Integer id) {
        ClientPojo client = clientDao.getById(id);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + id + " does not exist");
        }
        return client;
    }

    public void update(Integer id, ClientPojo clientPojo) {
        validateClientName(clientPojo);
        validateDuplicateClientWithSameName(clientPojo);
        ClientPojo existingClient = getCheckClientById(id);
        // Check if another client with the same name already exists (excluding current client)
        ClientPojo clientWithSameName = clientDao.getClientByName(clientPojo.getName());
        if (clientWithSameName != null && !clientWithSameName.getId().equals(id)) {
            throw new ApiException("Client with name already exists");
        }
        existingClient.setName(clientPojo.getName());
    }

    private void validateClientName(ClientPojo clientPojo) {
        if (Objects.isNull(clientPojo.getName())) {
            throw new ApiException("Client name cannot be null");
        }
        if (clientPojo.getName().trim().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        if (clientPojo.getName().length() > 100) {
            throw new ApiException("Client name cannot be longer than 100 characters");
        }
    }

    private void validateDuplicateClientWithSameName(ClientPojo clientPojo) {
        if (Objects.nonNull(clientDao.getClientByName(clientPojo.getName()))) {
            throw new ApiException("Client with name already exists");
        }
    }
}
