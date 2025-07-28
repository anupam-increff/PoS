package com.increff.pos.dto;

import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUploadUtil;
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
    private TSVDownloadService tsvDownloadService;

    public void addInventory(@Valid InventoryForm inventoryForm) {
        inventoryFlow.addInventory(inventoryForm.getBarcode(), inventoryForm.getQuantity());
    }

    public TSVUploadResponse uploadInventoryByTsv(MultipartFile file) {
        return TSVUploadUtil.processTSVUpload(
                file,
                InventoryForm.class,
                form -> inventoryFlow.addInventory(form.getBarcode(), form.getQuantity()),
                tsvDownloadService
        );
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        List<InventoryPojo> pojos = inventoryFlow.getAll(page, pageSize);
        long total = inventoryFlow.countAll();
        List<InventoryData> data = pojos.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        List<InventoryPojo> pojos = inventoryFlow.searchByBarcode(barcode, page, pageSize);
        long total = inventoryFlow.countByBarcodeSearch(barcode);
        List<InventoryData> data = pojos.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }

    public void updateInventoryByBarcode(String barcode, @Valid InventoryForm form) {
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }

    private InventoryData pojoToData(InventoryPojo pojo) {
        ProductPojo product = inventoryFlow.getProductById(pojo.getProductId());
        InventoryData data = ConvertUtil.convert(pojo, InventoryData.class);
        data.setBarcode(product.getBarcode());
        data.setName(product.getName());
        return data;
    }
}
