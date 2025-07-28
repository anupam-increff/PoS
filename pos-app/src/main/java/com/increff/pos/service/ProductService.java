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
        validateBarcode(productPojo.getBarcode());
        validateMrp(productPojo.getMrp());
        validateName(productPojo.getName());
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
        validateMrp(updated.getMrp());
        validateName(updated.getName());
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

    private void validateBarcode(String barcode) {
        if (Objects.isNull(barcode)) {
            throw new ApiException("Barcode cannot be null");
        }
        if (barcode.trim().isEmpty()) {
            throw new ApiException("Barcode cannot be empty");
        }
        ProductPojo existing = productDao.getByBarcode(barcode);
        if (!Objects.isNull(existing)) {
            throw new ApiException("Product with barcode " + barcode + " already exists");
        }
    }

    private void validateMrp(Double mrp) {
        if (Objects.isNull(mrp)) {
            throw new ApiException("MRP cannot be null");
        }
        if (mrp <= 0) {
            throw new ApiException("MRP must be positive");
        }
    }

    private void validateName(String name) {
        if (Objects.isNull(name)) {
            throw new ApiException("Name cannot be null");
        }
        if (name.trim().isEmpty()) {
            throw new ApiException("Name cannot be empty");
        }
    }
}
