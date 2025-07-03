package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Transactional
    public void addProduct(ProductPojo p) {
        if (!Objects.isNull(getCheckProductByBarcode(p.getBarcode()))) {
            throw new ApiException("Product with same barcode : " + p.getBarcode() + " already exists !");
        }
        dao.insert(p);
    }

    public ProductPojo getCheckProductById(Integer id) {
        ProductPojo productPojo = dao.getById(id);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("No such product exists ");
        }
        return productPojo;
    }

    public ProductPojo getCheckProductByBarcode(String barcode) {
        ProductPojo productPojo = dao.getByBarcode(barcode);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("No product with barcode : " + barcode + " exists");
        }
        return productPojo;
    }

    public List<ProductPojo> getAll() {
        return dao.getAll();
    }

    public List<ProductPojo> getProductsByClientId(Integer clientId) {
        return dao.getProductsByClientId(clientId);
    }

    @Transactional
    public void update(Integer id, ProductPojo newProductPojo) {
        ProductPojo existing = getCheckProductById(id);
        UpdateUtil.applyUpdates(existing, newProductPojo);
    }
}
