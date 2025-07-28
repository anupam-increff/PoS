package com.increff.pos.dto;

import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientDto {

    @Autowired
    private ClientService clientService;

    public void add(@Valid ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.addClient(pojo);
    }

    public PaginatedResponse<ClientData> getAll(Integer page, Integer pageSize) {
        List<ClientPojo> clients = clientService.getAllClients(page, pageSize);
        Long totalClients = clientService.countAll();
        List<ClientData> clientDataList = clients.stream()
                .map(pojo -> ConvertUtil.convert(pojo, ClientData.class))
                .collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(clientDataList, page, pageSize, totalClients);
    }

    public PaginatedResponse<ClientData> searchClients(String query, Integer page, Integer pageSize) {
        List<ClientPojo> clients = clientService.searchClients(query, page, pageSize);
        Long totalClients = clientService.countByQuery(query);
        List<ClientData> clientDataList = clients.stream()
                .map(pojo -> ConvertUtil.convert(pojo, ClientData.class))
                .collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(clientDataList, page, pageSize, totalClients);
    }

    public void update(Integer id, @Valid ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.update(id, pojo);
    }
}

