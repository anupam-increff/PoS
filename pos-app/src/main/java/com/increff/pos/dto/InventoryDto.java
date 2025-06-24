package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public void add(@Valid InventoryForm form) {
        ProductPojo product = productService.get(form.getProductId());
        if (product == null) {
            throw new ApiException("Product with ID " + form.getProductId() + " not found");
        }
        inventoryService.add(ConvertUtil.convert(form, InventoryPojo.class));
    }

    public List<InventoryData> getAll() {
        return inventoryService.getAll().stream().map(pojo -> {
            ProductPojo product = productService.get(pojo.getProductId());
            InventoryData data = new InventoryData();
            data.setId(pojo.getId());
            data.setQuantity(pojo.getQuantity());
            data.setBarcode(product.getBarcode());
            data.setName(product.getName());
            return data;
        }).collect(Collectors.toList());
    }

    public void updateByProductId(Integer productId, @Valid InventoryForm form) {
        if (!productId.equals(form.getProductId())) {
            throw new ApiException("Product ID mismatch between URL and body");
        }

        InventoryPojo existing = inventoryService.getByProductId(productId);
        if (existing == null) {
            throw new ApiException("No inventory found for productId: " + productId);
        }

        InventoryPojo updated = ConvertUtil.convert(form, InventoryPojo.class);
        inventoryService.update(existing.getId(), updated);
    }
}
