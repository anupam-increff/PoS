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
        if (Objects.isNull(clientPojo.getName())) {
            throw new ApiException("Client name cannot be null");
        }
        if (clientPojo.getName().trim().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        if (!Objects.isNull(clientDao.getClientByName(clientPojo.getName()))) {
            throw new ApiException("Client with name already exists");
        }
        clientDao.insert(clientPojo);
    }

    public List<ClientPojo> getAllClients(int page, int pageSize) {
        return clientDao.getAllClients(page, pageSize);
    }

    public long countAll() {
        return clientDao.countAll();
    }

    public List<ClientPojo> searchClients(String query, int page, int pageSize) {
        return clientDao.searchClientByName(query, page, pageSize);
    }

    public long countByQuery(String query) {
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
    
    public void update(int id, ClientPojo clientPojo) {
        ClientPojo existingClient = getCheckClientById(id);
        if (Objects.isNull(clientPojo.getName())) {
            throw new ApiException("Client name cannot be null");
        }
        if (clientPojo.getName().trim().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        // Check if another client with the same name already exists (excluding current client)
        ClientPojo clientWithSameName = clientDao.getClientByName(clientPojo.getName());
        if (clientWithSameName != null && !clientWithSameName.getId().equals(id)) {
            throw new ApiException("Client with name already exists");
        }
        existingClient.setName(clientPojo.getName());
    }
}
