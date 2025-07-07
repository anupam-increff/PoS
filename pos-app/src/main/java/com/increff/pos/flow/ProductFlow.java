package com.increff.pos.flow;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class ProductFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    public void addProduct(ProductForm form) {
        ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
        ClientPojo client = clientService.getCheckClientByName(form.getClientName());
        productPojo.setClientId(client.getId());
        productService.addProduct(productPojo);
    }

    public List<ProductPojo> getAllProducts(int page, int pageSize) {
        return productService.getAll(page, pageSize);
    }

    public long countAllProducts() {
        return productService.countAll();
    }

    public List<ProductPojo> getProductsByClient(String clientName, int page, int pageSize) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.getProductsByClientId(client.getId(), page, pageSize);
    }

    public long countProductsByClient(String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.countProductsByClientId(client.getId());
    }

    public List<ProductPojo> searchProductsByBarcode(String barcode, int page, int pageSize) {
        return productService.searchByBarcode(barcode, page, pageSize);
    }

    public long countSearchByBarcode(String barcode) {
        return productService.countSearchByBarcode(barcode);
    }

    public ProductData getProductByBarcode(String barcode) {
        ProductPojo productPojo = productService.getCheckProductByBarcode(barcode);
        return productPojoToProductData(productPojo);
    }

    public void updateProduct(Integer id, ProductForm form) {
        ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
        ClientPojo client = clientService.getCheckClientByName(form.getClientName());
        productPojo.setClientId(client.getId());
        productService.update(id, productPojo);
    }


    public ProductData productPojoToProductData(ProductPojo productPojo) {
        ProductData productData = ConvertUtil.convert(productPojo, ProductData.class);
        ClientPojo client = clientService.getCheckClientById(productPojo.getClientId());
        productData.setClientName(client.getName());
        return productData;
    }
}
