package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.InventoryData;
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

    public void uploadInventoryByTsv(MultipartFile file) {
        List<InventoryForm> forms = TSVUtil.readFromTsv(file, InventoryForm.class);
        
        if (forms.size() > MAX_ROWS) {
            throw new ApiException("Maximum " + MAX_ROWS + " rows allowed. Found: " + forms.size());
        }
        
        List<String> errors = new ArrayList<>();
        List<ErrorTSVData> errorDataList = new ArrayList<>();

        int rowNum = 1;
        for (InventoryForm form : forms) {
            try {
                addInventory(form);
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
                errorDataList.add(new ErrorTSVData(form.getBarcode(), form.getQuantity().toString(), e.getMessage()));
            } catch (Exception e) {
                errors.add("Row " + rowNum + ": Unexpected error - " + e.getMessage());
                errorDataList.add(new ErrorTSVData(form.getBarcode(), form.getQuantity().toString(), e.getMessage()));
            }
            rowNum++;
        }
        
        if (!errors.isEmpty()) {
            // Generate error TSV file with error messages
            byte[] errorTsvBytes = createErrorTsv(errorDataList);
            String errorMessage = "Inventory TSV upload failed. " + errors.size() + " errors found:\n" + String.join("\n", errors);
            throw new ApiException(errorMessage + "\nError TSV file generated with " + errorDataList.size() + " rows.");
        }
    }

    public void updateByBarcode(String barcode, @Valid InventoryForm inventoryForm) {
        if (!barcode.equals(inventoryForm.getBarcode())) {
            throw new ApiException("Barcode mismatch between path and form");
        }
        inventoryFlow.updateInventory(barcode, inventoryForm.getQuantity());
    }

    public static byte[] createErrorTsv(List<ErrorTSVData> errorDataList) {
        return TSVUtil.createErrorTsvFromList(errorDataList);
    }
}
