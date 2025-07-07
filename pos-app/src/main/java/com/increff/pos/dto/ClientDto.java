package com.increff.pos.dto;

import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.ConvertUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientDto {

    @Autowired
    private ClientService clientService;

    public void add(ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.addClient(pojo);
    }

    public PaginatedResponse<ClientData> getAll(int page, int pageSize) {
        List<ClientPojo> pojos = clientService.getAllClients(page, pageSize);
        long totalItems = clientService.countAll();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        List<ClientData> dataList = pojos.stream()
                .map(p -> ConvertUtil.convert(p, ClientData.class))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, pageSize);
    }

    public PaginatedResponse<ClientData> searchClients(String query, int page, int pageSize) {
        List<ClientPojo> pojos = clientService.searchClients(query, page, pageSize);
        long totalItems = clientService.countByQuery(query);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        List<ClientData> dataList = pojos.stream()
                .map(p -> ConvertUtil.convert(p, ClientData.class))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, pageSize);
    }

    public void update(int id, ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.update(id, pojo);
    }
}

