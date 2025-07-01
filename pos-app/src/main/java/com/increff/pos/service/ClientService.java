package com.increff.pos.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;

import javax.transaction.Transactional;

@Service
@Transactional
public class ClientService {
    @Autowired
    private ClientDao clientDao;

    public ClientPojo add(ClientPojo clientPojo) {
        if (clientDao.getClient(clientPojo.getName()) != null) {
            throw new ApiException("Client with name already exists");
        }
        clientDao.insert(clientPojo);
        return clientPojo;
    }

    public List<ClientData> getAllClients() {
        return clientDao.getAll().stream()
                .map(pojo -> {
                    return ConvertUtil.convert(pojo, ClientData.class);
                })
                .collect(Collectors.toList());
    }
    public ClientData getClient(String clientName){
        ClientPojo clientPojo= clientDao.getClient(clientName);
        return ConvertUtil.convert(clientPojo,ClientData.class);
    }

    public ClientData update(int id, ClientForm form) {

        ClientPojo existing = clientDao.getById(id);
        if (Objects.isNull(existing)) {
            throw new ApiException("Client with ID " + id + " does not exist");
        }

        ClientPojo duplicate = clientDao.getClient(form.getName());
        if (!Objects.isNull(duplicate) && !duplicate.getId().equals(id)) {
            throw new ApiException("Client Name already used by another client");
        }
        if(!Objects.isNull(duplicate) && duplicate.getName().equals(form.getName())){
            throw new ApiException("Client Name already set with the requested name");
        }

        existing.setName(form.getName());
        return ConvertUtil.convert(existing,ClientData.class);
    }

}
