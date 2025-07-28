package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
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

    public void addProduct(ProductPojo productPojo, String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        productPojo.setClientId(client.getId());
        productService.addProduct(productPojo);
    }

    public List<ProductPojo> getProductsByAClient(String clientName, int page, int pageSize) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.getProductsByClientId(client.getId(), page, pageSize);
    }

    public long countProductsByAClient(String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.countProductsByClientId(client.getId());
    }

    public void updateProduct(Integer id, ProductPojo productPojo, String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        productPojo.setClientId(client.getId());
        productService.update(id, productPojo);
    }
}
