package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class InventoryFlow {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Transactional
    public void updateInventory(String barcode, int quantity) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);
        inventoryService.updateInventory(product.getId(), quantity);
    }

    public List<InventoryData> getAll() {
        return inventoryService.getAll().stream().map(inventoryPojo -> {
            InventoryData inventoryData = ConvertUtil.convert(inventoryPojo, InventoryData.class);
            ProductPojo product = productService.getCheckProductById(inventoryPojo.getProductId());
            inventoryData.setBarcode(product.getBarcode());
            inventoryData.setName(product.getName());
            return inventoryData;
        }).collect(Collectors.toList());
    }

    public InventoryPojo getByBarcode(String barcode) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);

        InventoryPojo inventory = inventoryService.getCheckByProductId(product.getId());
        if (Objects.isNull(inventory)) {
            throw new ApiException("No inventory found for product with barcode: " + barcode);
        }
        return inventory;
    }
}