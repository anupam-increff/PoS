package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = ApiException.class)
public class InventoryFlow {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public void updateInventory(String barcode, int quantity) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);
        inventoryService.updateInventory(product.getId(), quantity);
    }

    public void addInventory(String barcode, int quantity) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);
        inventoryService.addInventory(product.getId(), quantity);
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        List<InventoryPojo> pojos = inventoryService.getAll(page, pageSize);
        long total = inventoryService.countAll();
        List<InventoryData> data = pojos.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        List<InventoryPojo> pojos = inventoryService.searchByBarcode(barcode, page, pageSize);
        long total = inventoryService.countByBarcodeSearch(barcode);
        List<InventoryData> data = pojos.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }

    private InventoryData pojoToData(InventoryPojo pojo) {
        ProductPojo product = productService.getCheckProductById(pojo.getProductId());
        InventoryData data = ConvertUtil.convert(pojo, InventoryData.class);
        data.setBarcode(product.getBarcode());
        data.setName(product.getName());
        return data;
    }
}