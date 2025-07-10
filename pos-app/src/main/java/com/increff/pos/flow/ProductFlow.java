package com.increff.pos.flow;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUtil;
import com.increff.pos.util.BulkUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class ProductFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    public void addProduct(ProductForm form) {
        ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
        ClientPojo client = clientService.getCheckClientByName(form.getClientName());
        productPojo.setClientId(client.getId());
        productService.addProduct(productPojo);
    }

    public TSVUploadResponse bulkUploadProducts(List<ProductForm> forms) {
        return BulkUploadUtil.processBulkUpload(
            forms,
            ProductForm.class,
            form -> {
                ProductPojo productPojo =ConvertUtil.convert(form,ProductPojo.class);
                ClientPojo client = clientService.getCheckClientByName(form.getClientName());
                productPojo.setClientId(client.getId());
                productService.addProduct(productPojo);
                return null;
            },
            tsvDownloadService,
            "error_upload",
            "product_upload"
        );
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
        ProductPojo productPojo = productService.getCheckProductByBarcode(barcode);
        return convertToProductData(productPojo);
    }

    public void updateProduct(Integer id, ProductForm form) {
        ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
        ClientPojo client = clientService.getCheckClientByName(form.getClientName());
        productPojo.setClientId(client.getId());
        productService.update(id, productPojo);
    }

    private ProductData convertToProductData(ProductPojo productPojo) {
        ProductData productData = ConvertUtil.convert(productPojo, ProductData.class);
        ClientPojo client = clientService.getCheckClientById(productPojo.getClientId());
        productData.setClientName(client.getName());
        return productData;
    }
}
