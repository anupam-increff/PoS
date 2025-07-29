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
import com.increff.pos.util.PaginationUtil;
import com.increff.pos.util.TSVUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto extends AbstractDto {

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public void addInventory(InventoryForm form) {
        checkValid(form);
        inventoryFlow.addInventory(form.getBarcode(), form.getQuantity());
    }

    public void updateInventoryByBarcode(String barcode, InventoryForm form) {
        checkValid(form);
        inventoryFlow.updateInventory(barcode, form.getQuantity());
    }

    public PaginatedResponse<InventoryData> getAll(Integer page, Integer pageSize) {
        List<InventoryPojo> inventories = inventoryService.getAll(page, pageSize);
        Long totalInventories = inventoryService.countAll();
        List<InventoryData> inventoryDataList = inventories.stream().map(this::convertToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(inventoryDataList, page, pageSize, totalInventories);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, Integer page, Integer pageSize) {
        List<InventoryPojo> inventories = inventoryService.searchByBarcode(barcode, page, pageSize);
        Long totalInventories = inventoryService.countByBarcodeSearch(barcode);
        List<InventoryData> inventoryDataList = inventories.stream().map(this::convertToData).collect(Collectors.toList());
        return PaginationUtil.createPaginatedResponse(inventoryDataList, page, pageSize, totalInventories);
    }

    public TSVUploadResponse uploadInventoryByTsv(MultipartFile file) {
        return TSVUploadUtil.processTSVUpload(
                file,
                InventoryForm.class,
                form -> {
                    checkValid(form);
                    inventoryFlow.addInventory(form.getBarcode(), form.getQuantity());
                }
        );
    }

    private InventoryData convertToData(InventoryPojo pojo) {
        ProductPojo product = productService.getCheckProductById(pojo.getProductId());
        InventoryData data = ConvertUtil.convert(pojo, InventoryData.class);
        data.setBarcode(product.getBarcode());
        data.setName(product.getName());
        return data;
    }
}
