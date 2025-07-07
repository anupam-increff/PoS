package com.increff.pos.service;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientDao clientDao;

    public void addClient(ClientPojo clientPojo) {
        if (!Objects.isNull(clientDao.getClientByName(clientPojo.getName()))) {
            throw new ApiException("Client with name already exists");
        }
        clientDao.insert(clientPojo);
    }

    public List<ClientPojo> getAllClients() {
        return new ArrayList<>(clientDao.getAll());
    }

    public List<ClientPojo> getAllClients(int page, int pageSize) {
        return clientDao.getAllPaged(page, pageSize);
    }

    public long countAll() {
        return clientDao.countAll();
    }

    public List<ClientPojo> searchClients(String query, int page, int pageSize) {
        return clientDao.searchByQuery(query, page, pageSize);
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
            throw new ApiException("Client with Id " + id + " does not exist");
        }
        return client;
    }

    public void update(int id, ClientPojo clientPojo) {
        ClientPojo existing = getCheckClientById(id);

        if (existing.getName().equals(clientPojo.getName())) {
            throw new ApiException("Client Name already set with the requested name");
        }

        ClientPojo duplicate = clientDao.getClientByName(clientPojo.getName());
        if (!Objects.isNull(duplicate) && !duplicate.getId().equals(id)) {
            throw new ApiException("Client Name already used by another client");
        }

        existing.setName(clientPojo.getName());
    }
}
