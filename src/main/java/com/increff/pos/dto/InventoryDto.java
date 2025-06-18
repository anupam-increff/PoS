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
    private InventoryService service;

    @Autowired
    private ProductService productService;

    public void add(@Valid InventoryForm form) {
        ProductPojo product = productService.get(form.getProductId());
        if (product == null) {
            throw new ApiException("Product with ID " + form.getProductId() + " not found");
        }
        service.add(ConvertUtil.convert(form,InventoryPojo.class));
    }

    public List<InventoryData> getAll() {
        return service.getAll().stream().map(pojo -> {
            ProductPojo product = productService.get(pojo.getProductId());
            InventoryData data = new InventoryData();
            data.setId(pojo.getId());
            data.setQuantity(pojo.getQuantity());
            data.setBarcode(product.getBarcode());
            data.setName(product.getName());
            return data;
        }).collect(Collectors.toList());

    }

    public void update(Integer id, InventoryForm inventoryForm) {
        service.update(id, ConvertUtil.convert(inventoryForm,InventoryPojo.class));
    }
}
