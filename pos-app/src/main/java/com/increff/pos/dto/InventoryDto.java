package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto extends BaseDto {

    private static final int MAX_ROWS = 5000;

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
        // Use TSVUtil to get the list
        List<InventoryForm> forms = TSVUtil.readFromTsv(file, InventoryForm.class);
        
        // Validate row count
        if (forms.size() > MAX_ROWS) {
            throw new ApiException("Maximum " + MAX_ROWS + " rows allowed. Found: " + forms.size());
        }
        
        // Pass to Flow for bulk upload
        return inventoryFlow.bulkUploadInventory(forms);
    }

    public void updateByBarcode(String barcode, @Valid InventoryForm inventoryForm) {
        if (!barcode.equals(inventoryForm.getBarcode())) {
            throw new ApiException("Barcode mismatch between path and form");
        }
        inventoryFlow.updateInventory(barcode, inventoryForm.getQuantity());
    }

}
