package com.increff.pos.controller;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientDto dto;

    @GetMapping
    public List<ClientData> getAll() {
        return dto.getAll();
    }

    @GetMapping("/{clientName}")
    public ClientData searchClient(@PathVariable String clientName) {
        return dto.getClient(clientName);
    }

    @PostMapping
    public void add(@RequestBody @Valid ClientForm form) {
        dto.add(form);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ClientForm form) {
        dto.update(id, form);
    }
}
