package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = ApiException.class)
public class ProductFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    public TSVUploadResponse processProductTSV(MultipartFile file) {
        return TSVUtil.processTSV(file, ProductForm.class, this::addProduct, "All products added successfully:");
    }

    public void addProduct(ProductForm form) {
        ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
        ClientPojo client = clientService.getCheckClientByName(form.getClientName());
        productPojo.setClientId(client.getId());
        productService.addProduct(productPojo);
    }

    public List<ProductData> getAllProducts(int page, int pageSize) {
        List<ProductPojo> products = productService.getAll(page, pageSize);
        return products.stream().map(this::convertToProductData).collect(Collectors.toList());
    }

    public long countAllProducts() {
        return productService.countAll();
    }

    public List<ProductData> getProductsByClient(String clientName, int page, int pageSize) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        List<ProductPojo> products = productService.getProductsByClientId(client.getId(), page, pageSize);
        return products.stream().map(this::convertToProductData).collect(Collectors.toList());
    }

    public long countProductsByClient(String clientName) {
        ClientPojo client = clientService.getCheckClientByName(clientName);
        return productService.countProductsByClientId(client.getId());
    }

    public List<ProductData> searchProductsByBarcode(String barcode, int page, int pageSize) {
        List<ProductPojo> products = productService.searchByBarcode(barcode, page, pageSize);
        return products.stream().map(this::convertToProductData).collect(Collectors.toList());
    }

    public long countSearchByBarcode(String barcode) {
        return productService.countSearchByBarcode(barcode);
    }

    public ProductData getProductByBarcode(String barcode) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);
        return convertToProductData(product);
    }

    public void updateProduct(Integer id, ProductForm form) {
        ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
        ClientPojo client = clientService.getCheckClientByName(form.getClientName());
        productPojo.setClientId(client.getId());
        productService.update(id, productPojo);
    }

    private ProductData convertToProductData(ProductPojo product) {
        ClientPojo client = clientService.getCheckClientById(product.getClientId());
        ProductData data = ConvertUtil.convert(product,ProductData.class);
        data.setClientName(client.getName());
        
        return data;
    }
}
