package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto {

    @Autowired
    private InventoryFlow inventoryFlow;

    public void processTsvUpload(MultipartFile file) {
        List<InventoryForm> forms = TSVUtil.readFromTsv(file, InventoryForm.class);
        List<String> errors = new ArrayList<>();

        int rowNum = 1;
        for (InventoryForm form : forms) {
            try {
                updateByBarcode(form.getBarcode(), form);
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
            }
            rowNum++;
        }

        if (!errors.isEmpty()) {
            throw new ApiException("Inventory TSV upload failed:\n" + String.join("\n", errors));
        }
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        return inventoryFlow.getAll(page, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        return inventoryFlow.searchByBarcode(barcode, page, pageSize);
    }

    public void updateByBarcode(String barcode, @Valid InventoryForm form) {
        if (!barcode.equals(form.getBarcode())) {
            throw new ApiException("Barcode mismatch between path and form");
        }
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }

    public void add(@Valid InventoryForm form) {
        inventoryFlow.addInventory(form.getBarcode(), form.getQuantity());
    }
}
