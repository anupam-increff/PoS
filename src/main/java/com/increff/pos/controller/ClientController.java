package com.increff.pos.controller;

import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService service;

    @GetMapping
    public List<ClientData> getAll() {
        return service.getAll();
    }

    @PostMapping
    public void add(@RequestBody ClientForm form) {
        service.add(form);
    }
}
