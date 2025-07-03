package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto inventoryDto;


    @GetMapping
    public List<InventoryData> getAllInventory() {
        return inventoryDto.getAll();
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadInventory(@RequestParam("file") MultipartFile file) {
        inventoryDto.processTsvUpload(file);
    }

    @PutMapping(path = "/{barcode}")
    public void updateInventory(@PathVariable String barcode, @RequestBody @Valid InventoryForm form) {
        inventoryDto.updateByBarcode(barcode, form);
    }
}
