package com.increff.pos.dto;

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

    public void add(@Valid ClientForm form) {
        ClientPojo pojo = ConvertUtil.convert(form, ClientPojo.class);
        clientService.addClient(pojo);
    }

    public List<ClientData> getAll() {
        return clientService.getAllClients().stream().
                map(p -> ConvertUtil.convert(p, ClientData.class)).
                collect(Collectors.toList());
    }

    public ClientData getClient(String clientName) {
        ClientPojo clientPojo = clientService.getCheckClientByName(clientName);
        return ConvertUtil.convert(clientPojo, ClientData.class);
    }

    public void update(int id, @Valid ClientForm clientForm) {
        ClientPojo clientPojo = ConvertUtil.convert(clientForm, ClientPojo.class);
        clientService.update(id, clientPojo);
    }
}
