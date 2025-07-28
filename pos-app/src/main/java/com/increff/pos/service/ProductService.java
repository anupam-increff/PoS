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
@Transactional(rollbackFor = ApiException.class)
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public void addProduct(ProductPojo productPojo) {
        checkDuplicateBarcode(productPojo.getBarcode());
        productDao.insert(productPojo);
    }

    public void validateSellingPrice(Double sellingPrice, ProductPojo product) {
        if (sellingPrice > product.getMrp()) {
            throw new ApiException("Selling price for product " + product.getName() + " cannot be greater than its mrp " + product.getMrp());
        }
    }

    public List<ProductPojo> getAll(Integer page, Integer pageSize) {
        return productDao.getAllProducts(page, pageSize);
    }

    public Long countAll() {
        return productDao.countAll();
    }

    public List<ProductPojo> searchByBarcode(String barcode, Integer page, Integer pageSize) {
        return productDao.searchByBarcode(barcode, page, pageSize);
    }

    public Long countSearchByBarcode(String barcode) {
        return productDao.countByBarcodeSearch(barcode);
    }

    public ProductPojo getCheckProductByBarcode(String barcode) {
        ProductPojo product = productDao.getByBarcode(barcode);
        if (Objects.isNull(product)) {
            throw new ApiException("Product with barcode " + barcode + " not found");
        }
        return product;
    }

    public ProductPojo getCheckProductById(Integer id) {
        ProductPojo product = productDao.getById(id);
        if (Objects.isNull(product)) {
            throw new ApiException("Product with ID " + id + " not found");
        }
        return product;
    }

    public void update(Integer id, ProductPojo updated) {
        ProductPojo existing = getCheckProductById(id);
        existing.setName(updated.getName());
        existing.setMrp(updated.getMrp());
        existing.setClientId(updated.getClientId());
    }

    public List<ProductPojo> getProductsByClientId(Integer clientId, Integer page, Integer pageSize) {
        return productDao.getProductsByClientId(clientId, page, pageSize);
    }

    public Long countProductsByClientId(Integer clientId) {
        return productDao.countByClientId(clientId);
    }

    private void checkDuplicateBarcode(String barcode) {
        ProductPojo existing = productDao.getByBarcode(barcode);
        if (Objects.nonNull(existing)) {
            throw new ApiException("Product with barcode " + barcode + " already exists");
        }
    }
}
