package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = ApiException.class)
public class ProductFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    public void addProduct(ProductPojo productPojo , String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        productPojo.setClientId(client.getId());
        productService.addProduct(productPojo);
    }

    public List<ProductPojo> getAllProducts(int page, int pageSize) {
        return productService.getAll(page, pageSize);
    }

    public long countAllProducts() {
        return productService.countAll();
    }

    public List<ProductPojo> getProductsByAClient(String clientName, int page, int pageSize) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.getProductsByClientId(client.getId(), page, pageSize);
    }

    public long countProductsByAClient(String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.countProductsByClientId(client.getId());
    }

    public List<ProductPojo> searchProductsByBarcode(String barcode, int page, int pageSize) {
        return productService.searchByBarcode(barcode, page, pageSize);
    }

    public long countSearchByBarcode(String barcode) {
        return productService.countSearchByBarcode(barcode);
    }

    public ProductPojo getProductByBarcode(String barcode) {
        return productService.getCheckProductByBarcode(barcode);
    }

    public void updateProduct(Integer id, ProductPojo productPojo, String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        productPojo.setClientId(client.getId());
        productService.update(id, productPojo);
    }
}
