package com.increff.pos.dto;

import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientDto extends BaseDto {

    @Autowired
    private ClientService clientService;

    public void add(@Valid ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.addClient(pojo);
    }

    public PaginatedResponse<ClientData> getAll(int page, int pageSize) {
        List<ClientPojo> clientPojos = clientService.getAllClients(page, pageSize);
        long totalItems = clientService.countAll();
        return createPaginatedResponse(clientPojos.stream()
                .map(p -> ConvertUtil.convert(p, ClientData.class))
                .collect(Collectors.toList()), page, pageSize, totalItems);
    }

    public PaginatedResponse<ClientData> searchClients(String query, int page, int pageSize) {
        List<ClientPojo> pojos = clientService.searchClients(query, page, pageSize);
        long totalItems = clientService.countByQuery(query);
        return createPaginatedResponse(pojos.stream()
                .map(p -> ConvertUtil.convert(p, ClientData.class))
                .collect(Collectors.toList()), page, pageSize, totalItems);
    }

    public void update(int id,@Valid ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.update(id, pojo);
    }

}

