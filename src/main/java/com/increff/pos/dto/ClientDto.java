package com.increff.pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.dao.ClientDao;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.ConvertUtil;

@Component
public class ClientDto {
    @Autowired
    private ClientDao dao;

    public void validate(ClientForm f) {
        if (f.getName() == null || f.getName().isEmpty()) {
            throw new IllegalArgumentException("Name can't be blank");
        }
        if (!f.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public ClientData create(ClientForm f) {
        ClientPojo p = ConvertUtil.formToPojo(f);
        dao.insert(p);
        return toData(p);
    }

    public ClientData toData(ClientPojo p) {
        return ConvertUtil.pojoToData(p);
    }
}
