package com.increff.pos.controller;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
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
    public PaginatedResponse<ClientData> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return dto.getAll(page, pageSize);
    }

    @GetMapping("/search")
    public PaginatedResponse<ClientData> searchClients(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return dto.searchClients(query, page, pageSize);
    }

    @PostMapping
    public void add(@RequestBody @Valid ClientForm form) {
        dto.add(form);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody @Valid ClientForm form) {
        dto.update(id, form);
    }
}
