package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @PostMapping
    public void add(@RequestBody @Valid InventoryForm form) {
        dto.add(form);
    }

    @GetMapping
    public List<InventoryData> getAll() {
        return dto.getAll();
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Integer id,@RequestBody @Valid InventoryForm inventoryForm) {
        dto.update(id, inventoryForm);
    }
}
