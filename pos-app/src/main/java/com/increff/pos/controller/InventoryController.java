package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto inventoryDto;

    @GetMapping
    public PaginatedResponse<InventoryData> getAllInventory(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        return inventoryDto.getAll(page, pageSize);
    }

    @GetMapping("/search")
    public PaginatedResponse<InventoryData> searchInventory(@RequestParam String barcode,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        return inventoryDto.searchByBarcode(barcode, page, pageSize);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadInventory(@RequestParam("file") MultipartFile file) {
        inventoryDto.processTsvUpload(file);
    }

    @PostMapping
    public void addInventory(@RequestBody @Valid InventoryForm form) {
        inventoryDto.add(form);
    }

    @PutMapping(path = "/{barcode}")
    public void updateInventory(@PathVariable String barcode, @RequestBody @Valid InventoryForm form) {
        inventoryDto.updateByBarcode(barcode, form);
    }
}
