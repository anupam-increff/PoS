package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.increff.pos.dao.ClientDao;

import javax.transaction.Transactional;

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

    public ClientPojo getCheckClientByName(String clientName) throws ApiException {
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
        ClientPojo duplicate = clientDao.getClientByName(clientPojo.getName());
        if (!Objects.isNull(duplicate)) {
            if (!duplicate.getId().equals(id)) {
                throw new ApiException("Client Name already used by another client");
            } else {
                throw new ApiException("Client Name already set with the requested name");
            }
        }
        ClientPojo existing = getCheckClientById(id);
        existing.setName(clientPojo.getName());

    }
}
