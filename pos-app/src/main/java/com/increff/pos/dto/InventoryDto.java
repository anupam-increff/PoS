package com.increff.pos.dto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;

    public void processTsvUpload(MultipartFile file) {
        List<InventoryForm> forms = TSVUtil.readFromTsv(file, InventoryForm.class);
        List<String> errors = new ArrayList<>();

        int rowNum = 1;
        for (InventoryForm form : forms) {
            try {
                inventoryService.updateInventory(form.getBarcode(),form.getQuantity());
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
            }
            rowNum++;
        }

        if (!errors.isEmpty()) {
            throw new ApiException("Inventory TSV upload failed:\n" + String.join("\n", errors));
        }
    }

    public List<InventoryData> getAll() {
        return inventoryService.getAll();
    }

    public void updateByBarcode(String barcode, @Valid InventoryForm form) {
        if (!barcode.equals(form.getBarcode())) {
            throw new ApiException("Barcode mismatch between path and form");
        }
        inventoryService.updateInventory(barcode, form.getQuantity());
    }
}
