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

@Component
public class ClientDto {
    @Autowired
    private ClientService service ;

    public ClientData add(@Valid ClientForm form) {

        ClientPojo pojo = ConvertUtil.convert(form,ClientPojo.class);
        ClientPojo saved =service.add(pojo); // send POJO to service
        return ConvertUtil.convert(saved,ClientData.class); // convert POJO to response format
    }
    public List<ClientData> getAll(){
        return service.getAllClients();
    }

    public ClientData update(int id, @Valid ClientForm form) {
        return service.update(id,form);
    }
}
