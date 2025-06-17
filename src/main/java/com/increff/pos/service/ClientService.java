package com.increff.pos.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;

@Service
public class ClientService {
    @Autowired
    private ClientDao dao;
    @Autowired
    private ClientDto dto;

    @Transactional
    public ClientData add(ClientForm form) {
        dto.validate(form);
        return dto.create(form);
    }

    @Transactional
    public List<ClientData> getAll() {
        return dao.selectAll().stream()
                .map(dto::toData)
                .collect(Collectors.toList());
    }
}
