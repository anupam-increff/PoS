package com.increff.pos.controller;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientDto clientDto;

    @ApiOperation("Add a new client")
    @PostMapping
    public void addClient(@RequestBody ClientForm clientForm) {
        clientDto.add(clientForm);
    }

    @ApiOperation("Get all clients")
    @GetMapping
    public PaginatedResponse<ClientData> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return clientDto.getAll(page, pageSize);
    }

    @ApiOperation("Search clients by query")
    @GetMapping("/search")
    public PaginatedResponse<ClientData> searchClients(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return clientDto.searchClients(query, page, pageSize);
    }

    @ApiOperation("Update client by ID")
    @PutMapping("/{id}")
    public void updateClient(@PathVariable Integer id, @RequestBody ClientForm clientForm) {
        clientDto.update(id, clientForm);
    }
}
