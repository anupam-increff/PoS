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
        inventory.setQuantity(quantity);
    }

    @Transactional
    public void addInventory(Integer productId, Integer quantity) {
        InventoryPojo inventory = inventoryDao.getByProductId(productId);
        if (Objects.isNull(inventory)) {
            InventoryPojo newInventory = new InventoryPojo();
            newInventory.setProductId(productId);
            newInventory.setQuantity(quantity);
            inventoryDao.insert(newInventory);
        } else {
            throw new ApiException("Inventory already exists for this product/barcode");
        }
    }

    public List<InventoryPojo> getAll(int page, int pageSize) {
        return inventoryDao.getAllPaginated(page, pageSize);
    }

    public long countAll() {
        return inventoryDao.countAll();
    }

    public List<InventoryPojo> searchByBarcode(String barcode, int page, int pageSize) {
        return inventoryDao.searchByBarcode(barcode, page, pageSize);
    }

    public long countByBarcodeSearch(String barcode) {
        return inventoryDao.countByBarcodeSearch(barcode);
    }

    public InventoryPojo getCheckByProductId(Integer productId) {
        InventoryPojo inventoryPojo = inventoryDao.getByProductId(productId);
        if (Objects.isNull(inventoryPojo)) throw new ApiException("No Inventory data found for this product");
        return inventoryPojo;
    }

}