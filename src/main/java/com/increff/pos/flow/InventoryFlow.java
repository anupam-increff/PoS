package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryFlow {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public void add(@Valid InventoryForm form) {
        ProductPojo product = productService.get(form.getProductId());
        if (product == null) {
            throw new ApiException("Product with ID " + form.getProductId() + " not found");
        }
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(form.getProductId());
        pojo.setQuantity(form.getQuantity());
        inventoryService.add(pojo);
    }

    public void update(Integer id, @Valid InventoryForm form) {
        if (!id.equals(form.getProductId())) {
            throw new ApiException("Wrong productId combination!");
        }
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(form.getProductId());
        pojo.setQuantity(form.getQuantity());
        inventoryService.update(id, pojo);
    }

    public List<InventoryData> getAll() {
        List<InventoryPojo> inventoryList = inventoryService.getAll();
        return inventoryList.stream().map(inv -> {
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
