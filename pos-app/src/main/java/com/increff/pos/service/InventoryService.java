package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProductService productService;

    @Transactional
    public void updateInventory(String barcode, int quantity) {
        InventoryPojo inventory = getByBarcode(barcode);
        if (inventory == null) {
            // Create new inventory entry
            InventoryPojo newInventory = new InventoryPojo();
            newInventory.setProductId(inventory.getProductId());
            newInventory.setQuantity(quantity);
            inventoryDao.insert(newInventory);
        } else {
            // add quantity to existing inventory
            inventory.setQuantity(inventory.getQuantity() + quantity);
            inventoryDao.update(inventory);
        }
    }
    public List<InventoryData> getAll() {
        return inventoryDao.selectAll().stream().map(pojo -> {
            ProductPojo product = productService.get(pojo.getProductId());
            InventoryData data = new InventoryData();
            data.setId(pojo.getId());
            data.setProductId(product.getId());
            data.setBarcode(product.getBarcode());
            data.setName(product.getName());
            data.setQuantity(pojo.getQuantity());
            return data;
        }).collect(Collectors.toList());
    }
    public InventoryPojo getByBarcode(String barcode) {
        ProductPojo product = productService.getByBarcode(barcode);
        if (product == null) {
            throw new ApiException("Product with barcode " + barcode + " not found");
        }
        InventoryPojo inventory = getByProductId(product.getId());
        if (inventory == null) {
            throw new ApiException("No inventory found for product with barcode: " + barcode);
        }
        return inventory;
    }
    public InventoryPojo getByProductId(Integer productId) {
        ProductPojo product = productService.get(productId);
        if (product == null) {
            throw new ApiException("Product with productId" + productId + " not found");
        }
        return inventoryDao.selectByProductId(productId);
    }

}