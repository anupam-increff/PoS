package com.increff.pos.dto;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto extends BaseDto {

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public void addInventory(@Valid InventoryForm form) {
        inventoryFlow.addInventory(form.getBarcode(), form.getQuantity());
    }

    public void updateInventoryByBarcode(String barcode, @Valid InventoryForm form) {
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        List<InventoryPojo> inventories = inventoryService.getAll(page, pageSize);
        long totalInventories = inventoryService.countAll();
        List<InventoryData> inventoryDataList = inventories.stream().map(this::convertToData).collect(Collectors.toList());
        return new PaginatedResponse<>(inventoryDataList, page, (int) Math.ceil((double) totalInventories / pageSize), totalInventories, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        List<InventoryPojo> inventories = inventoryService.searchByBarcode(barcode, page, pageSize);
        long totalInventories = inventoryService.countByBarcodeSearch(barcode);
        List<InventoryData> inventoryDataList = inventories.stream().map(this::convertToData).collect(Collectors.toList());
        return new PaginatedResponse<>(inventoryDataList, page, (int) Math.ceil((double) totalInventories / pageSize), totalInventories, pageSize);
    }

    public TSVUploadResponse uploadInventoryByTsv(MultipartFile file) {
        // TODO: Implement TSV upload logic
        return TSVUploadResponse.success("File uploaded successfully", 0);
    }

    private InventoryData convertToData(InventoryPojo pojo) {
        ProductPojo product = productService.getCheckProductById(pojo.getProductId());
        InventoryData data = ConvertUtil.convert(pojo, InventoryData.class);
        data.setBarcode(product.getBarcode());
        data.setName(product.getName());
        return data;
    }
}
