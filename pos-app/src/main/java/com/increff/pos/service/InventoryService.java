package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryDao dao;

    @Transactional
    public void add(InventoryPojo p) {
        InventoryPojo existing = dao.selectByProductId(p.getProductId());
        if (existing == null) dao.insert(p);
        else {
            existing.setQuantity(existing.getQuantity() + p.getQuantity());
            //dao.update(existing);
        }
    }

    public InventoryPojo get(Integer id) {
        return dao.select(id);
    }
    public InventoryPojo getByProductId(Integer productId) {
        return dao.selectByProductId(productId);
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public void update(Integer id, InventoryPojo updatedInventoryPojo) {
        InventoryPojo existing = dao.select(id);
        if (existing == null) {
            throw new ApiException("Inventory with Id : " + id + " not found");
        }
        existing.setQuantity(updatedInventoryPojo.getQuantity());
        //dao.update(existing);
    }

}