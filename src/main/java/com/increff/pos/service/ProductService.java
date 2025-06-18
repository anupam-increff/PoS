package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Transactional
    public void add(ProductPojo p) {
        if (dao.selectByBarcode(p.getBarcode()) != null) {
            throw new RuntimeException("Barcode must be unique");
        }
        dao.insert(p);
    }

    public ProductPojo get(Integer id) {
        return dao.select(id);
    }

    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public void update(Integer id, ProductPojo p) {
        ProductPojo existing = dao.select(id);
        if (existing == null) throw new ApiException("Product not found");
        existing.setBarcode(p.getBarcode());
        existing.setName(p.getName());
        existing.setMrp(p.getMrp());
        existing.setImageUrl(p.getImageUrl());
        dao.update(existing);
    }
}
