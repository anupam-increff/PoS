package com.increff.pos.service;

import java.util.List;
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
    private ClientDao dao;

    public ClientPojo add(ClientPojo clientPojo) {
        if (dao.getClient(clientPojo.getName()) != null) {
            throw new ApiException("Client with name already exists");
        }
        dao.insert(clientPojo);
        return clientPojo;
    }

    public List<ClientData> getAllClients() {
        return dao.selectAll().stream()
                .map(pojo -> {
                    return ConvertUtil.convert(pojo, ClientData.class);
                })
                .collect(Collectors.toList());
    }

    public ClientData update(int id, ClientForm form) {

        ClientPojo existing = dao.getById(id);
        if (existing == null) {
            throw new ApiException("Client with ID " + id + " does not exist");
        }
        ClientPojo duplicate = dao.getClient(form.getName());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ApiException("Client Name already used by another client");
        }
        if(duplicate!=null && duplicate.getName().equals(form.getName())){
            throw new ApiException("Client Name already set with the requested name");
        }
        existing.setName(form.getName());
        //dao.update(existing);
        return ConvertUtil.convert(existing,ClientData.class);
    }

}
