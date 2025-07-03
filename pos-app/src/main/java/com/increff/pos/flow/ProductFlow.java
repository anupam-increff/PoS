package com.increff.pos.flow;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class ProductFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    public void addProduct(ProductPojo productPojo) {
        ClientPojo client = clientService.getCheckClientById(productPojo.getClientId());
        productService.addProduct(productPojo);
    }

    public List<ProductData> getAllProducts() {
        List<ProductPojo> productPojos = productService.getAll();
        return productPojoToProductData(productPojos);
    }

    public List<ProductData> getProductsByClient(String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        List<ProductPojo> productPojos = productService.getProductsByClientId(client.getId());
        return productPojoToProductData(productPojos);
    }

    public ProductPojo getProductByBarcode(String barcode) {
        return productService.getCheckProductByBarcode(barcode);
    }

    public void updateProduct(Integer id, ProductPojo productPojo) {
        productService.update(id, productPojo);
    }

    private List<ProductData> productPojoToProductData(List<ProductPojo> productPojos) {
        return productPojos.stream().map(productPojo -> {
            ProductData productData = ConvertUtil.convert(productPojo, ProductData.class);
            // Set client name
            ClientPojo client = clientService.getCheckClientById(productPojo.getClientId());
            productData.setClientName(client.getName());
            return productData;
        }).collect(Collectors.toList());
    }
}
