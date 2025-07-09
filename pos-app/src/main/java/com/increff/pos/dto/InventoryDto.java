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
public class InventoryDto extends BaseDto {

    private static final int MAX_ROWS = 5000;

    @Autowired
    private InventoryFlow inventoryFlow;

    public void processTsvUpload(MultipartFile file) {
        List<InventoryForm> forms = TSVUtil.readFromTsv(file, InventoryForm.class);
        
        if (forms.size() > MAX_ROWS) {
            throw new ApiException("Maximum " + MAX_ROWS + " rows allowed. Found: " + forms.size());
        }
        
        List<String> errors = new ArrayList<>();
        List<InventoryForm> errorRows = new ArrayList<>();

        int rowNum = 1;
        for (InventoryForm form : forms) {
            try {
                updateByBarcode(form.getBarcode(), form);
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
                errorRows.add(form);
            }
            rowNum++;
        }

        if (!errors.isEmpty()) {
            // Generate error TSV file
            byte[] errorTsvBytes = TSVUtil.createTsvFromList(errorRows, InventoryForm.class);
            String errorMessage = "Inventory TSV upload failed. " + errors.size() + " errors found:\n" + String.join("\n", errors);
            throw new ApiException(errorMessage + "\nError TSV file generated with " + errorRows.size() + " rows.");
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
