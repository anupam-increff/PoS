package com.increff.pos.flow;

import com.increff.pos.model.data.InventoryData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryFlow {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public List<InventoryData> getAllInventoryWithProductInfo() {
        List<InventoryPojo> list = inventoryService.getAll();

        return list.stream().map(inv -> {
            ProductPojo product = productService.get(inv.getProductId());

            InventoryData data = new InventoryData();
            data.setId(inv.getId());
            data.setQuantity(inv.getQuantity());
            data.setBarcode(product.getBarcode());
            data.setName(product.getName());
            return data;
        }).collect(Collectors.toList());
    }
}
