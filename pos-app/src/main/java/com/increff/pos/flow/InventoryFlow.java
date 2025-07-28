package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
}