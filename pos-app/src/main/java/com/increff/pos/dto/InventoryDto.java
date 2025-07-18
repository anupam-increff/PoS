package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.util.TSVUtil;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Component
public class InventoryDto extends BaseDto {

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

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
        try {
            List<InventoryForm> formList = TSVUtil.readFromTsv(file, InventoryForm.class);
            List<String[]> rawRows = TSVUtil.readRawRows(file);

            if (formList.isEmpty()) {
                throw new ApiException("TSV file is empty or has no valid data");
            }

            List<String> successList = new ArrayList<>();
            List<String> failureList = new ArrayList<>();

            for (int i = 0; i < formList.size(); i++) {
                InventoryForm form = formList.get(i);
                try {
                    ValidationUtil.validate(form);
                    inventoryFlow.addInventory(form.getBarcode(), form.getQuantity());
                    successList.add("Row " + (i + 1));
                } catch (ApiException e) {
                    String[] row = Arrays.copyOf(rawRows.get(i), rawRows.get(i).length + 1);
                    row[row.length - 1] = e.getMessage();
                    failureList.add(String.join("\t", row));
                }
            }

            if (!failureList.isEmpty()) {
                String[] errorHeaders = {"Barcode","Quantity","Error"};
                List<String[]> errorRows = failureList.stream().map(s->s.split("\t",-1)).collect(java.util.stream.Collectors.toList());
                byte[] errorTsv = TSVUtil.createTsvFromRows(errorRows, errorHeaders);
                String fileId = tsvDownloadService.storeTSVFile(errorTsv, "inventory_upload_errors.tsv");
                TSVUploadResponse resp = TSVUploadResponse.error("TSV processing completed with errors. " + successList.size() + " inventory items updated successfully.", formList.size(), failureList.size(), failureList);
                resp.setDownloadUrl("/tsv/download/" + fileId);
                return resp;
            } else {
                return TSVUploadResponse.success("All " + successList.size() + " inventory items updated successfully", successList.size());
            }

        } catch (Exception e) {
            throw new ApiException("Failed to process TSV file: " + e.getMessage(), e);
        }
    }

    public void updateInventoryByBarcode(String barcode, @Valid InventoryForm form) {
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }
}
