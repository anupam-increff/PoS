package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    @Transactional
    public void updateInventory(Integer productId, Integer quantity) {
        InventoryPojo inventory = getCheckByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.getAll();
    }

    public InventoryPojo getCheckByProductId(Integer productId) {
        InventoryPojo inventoryPojo = inventoryDao.getByProductId(productId);
        if (Objects.isNull(inventoryPojo))
            throw new ApiException("No Inventory data found for this product ");
        return inventoryPojo;
    }

}