package com.increff.pos.dto;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.TSVUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Component
public class InventoryDto extends BaseDto {

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    public void addInventory(@Valid InventoryForm inventoryForm) {
        inventoryFlow.addInventory(inventoryForm.getBarcode(), inventoryForm.getQuantity());
    }

    public TSVUploadResponse uploadInventoryByTsv(MultipartFile file) {
        String[] errorHeaders = {"Barcode", "Quantity", "Error"};
        String errorFileName = "inventory_upload_errors.tsv";
        final int maxRows = 5000;
        String successMessage = "inventory items updated successfully";

        return TSVUploadUtil.processTSVUpload(
            file, InventoryForm.class, errorHeaders, errorFileName, maxRows,
            form -> inventoryFlow.addInventory(form.getBarcode(), form.getQuantity()),
            successMessage, tsvDownloadService
        );
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        return inventoryFlow.getAll(page, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        return inventoryFlow.searchByBarcode(barcode, page, pageSize);
    }

    public void updateInventoryByBarcode(String barcode, @Valid InventoryForm form) {
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }
}
