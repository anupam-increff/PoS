package com.increff.pos.dto;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Component
public class InventoryDto extends BaseDto {

    @Autowired
    private InventoryFlow inventoryFlow;

    public void addInventory(@Valid InventoryForm inventoryForm) {
        inventoryFlow.addInventory(inventoryForm.getBarcode(), inventoryForm.getQuantity());
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        return inventoryFlow.getAll(page, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        return inventoryFlow.searchByBarcode(barcode, page, pageSize);
    }

    public TSVUploadResponse uploadInventoryByTsv(MultipartFile file) {
        return inventoryFlow.processInventoryTSV(file);
    }

    public void updateInventoryByBarcode(String barcode, @Valid InventoryForm form) {
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }
}
