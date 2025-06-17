package com.increff.pos.util;

import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;

public class ConvertUtil {

    public static ClientPojo formToPojo(ClientForm f) {
        ClientPojo p = new ClientPojo();
        p.setName(f.getName().trim());
        p.setEmail(f.getEmail().trim().toLowerCase());
        return p;
    }

    public static ClientData pojoToData(ClientPojo p) {
        return ClientData.builder()
                .id(p.getId())
                .name(p.getName())
                .email(p.getEmail())
                .build();
    }
}
