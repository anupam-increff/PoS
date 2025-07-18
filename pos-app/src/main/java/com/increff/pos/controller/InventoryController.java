package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("Get all inventory items")
    @GetMapping
    public PaginatedResponse<InventoryData> getAllInventory(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        return inventoryDto.getAll(page, pageSize);
    }

    @ApiOperation("Search inventory by barcode")
    @GetMapping("/search")
    public PaginatedResponse<InventoryData> searchInventory(@RequestParam String barcode,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        return inventoryDto.searchByBarcode(barcode, page, pageSize);
    }

    @ApiOperation("Upload inventory data via TSV file (Supervisor only)")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TSVUploadResponse uploadInventory(@RequestParam("file") MultipartFile file) {
        return inventoryDto.uploadInventoryByTsv(file);
    }

    @ApiOperation("Add new inventory item")
    @PostMapping
    public void addInventory(@RequestBody @Valid InventoryForm form) {
        inventoryDto.addInventory(form);
    }

    @ApiOperation("Update inventory by barcode")
    @PutMapping
    public void updateInventory(@RequestBody @Valid InventoryForm form) {
        inventoryDto.updateInventoryByBarcode(form.getBarcode(), form);
    }
}
