package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    public void addProduct(ProductPojo p) {
        if (!Objects.isNull(getProductByBarcode(p.getBarcode()))) {
            throw new ApiException("Product with same barcode : " + p.getBarcode() + " already exists !");
        }
        dao.insert(p);
    }

    public ProductPojo getCheckProductById(Integer id) {
        ProductPojo productPojo = dao.getById(id);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("No such product exists");
        }
        return productPojo;
    }
    public ProductPojo getProductByBarcode(String barcode) {

        return dao.getByBarcode(barcode);
    }
    public ProductPojo getCheckProductByBarcode(String barcode) {
        ProductPojo productPojo = dao.getByBarcode(barcode);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("No product with barcode : " + barcode + " exists");
        }
        return productPojo;
    }

    public List<ProductPojo> getAll(int page, int pageSize) {
        return dao.getAllPaged(page, pageSize);
    }

    public long countAll() {
        return dao.countAll();
    }

    public List<ProductPojo> getProductsByClientId(Integer clientId, int page, int pageSize) {
        return dao.getByClientIdPaged(clientId, page, pageSize);
    }

    public long countProductsByClientId(Integer clientId) {
        return dao.countByClientId(clientId);
    }

    public List<ProductPojo> searchByBarcode(String barcode, int page, int pageSize) {
        return dao.searchByBarcode(barcode, page, pageSize);
    }

    public long countSearchByBarcode(String barcode) {
        return dao.countByBarcodeSearch(barcode);
    }

    public void update(Integer id, ProductPojo newProductPojo) {
        ProductPojo productPojo = getCheckProductById(id);
        productPojo.setName(newProductPojo.getName());
        productPojo.setMrp(newProductPojo.getMrp());
        productPojo.setImageUrl(newProductPojo.getImageUrl());
    }
}
