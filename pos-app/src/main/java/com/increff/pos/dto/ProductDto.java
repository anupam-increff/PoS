package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDto extends BaseDto {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    public void addProduct(@Valid ProductForm productForm) {
        ProductPojo productPojo = ConvertUtil.convert(productForm, ProductPojo.class);
        productFlow.addProduct(productPojo, productForm.getClientName());
    }

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        return TSVUploadUtil.processTSVUpload(file, ProductForm.class, form -> {
            ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
            productFlow.addProduct(productPojo, form.getClientName());
        }, tsvDownloadService);
    }

    public PaginatedResponse<ProductData> getAll(int page, int pageSize) {
        List<ProductPojo> products = productFlow.getAllProducts(page, pageSize);
        List<ProductData> productData = products.stream().map(this::pojoToData).collect(Collectors.toList());
        long total = productFlow.countAllProducts();
        return createPaginatedResponse(productData, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, int page, int pageSize) {
        List<ProductPojo> products = productFlow.getProductsByAClient(clientName, page, pageSize);
        List<ProductData> productData = products.stream().map(this::pojoToData).collect(Collectors.toList());
        long total = productFlow.countProductsByAClient(clientName);
        return createPaginatedResponse(productData, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> searchByBarcode(String barcode, int page, int pageSize) {
        List<ProductPojo> products = productFlow.searchProductsByBarcode(barcode, page, pageSize);
        List<ProductData> productData = products.stream().map(this::pojoToData).collect(Collectors.toList());
        long total = productFlow.countSearchByBarcode(barcode);
        return createPaginatedResponse(productData, page, pageSize, total);
    }

    public ProductData getByBarcode(String barcode) {
        ProductPojo product = productFlow.getProductByBarcode(barcode);
        return pojoToData(product);
    }

    public void update(Integer id, @Valid ProductForm productForm) {
        ProductPojo productPojo = ConvertUtil.convert(productForm, ProductPojo.class);
        productFlow.updateProduct(id, productPojo, productForm.getClientName());
    }

    private ProductData pojoToData(ProductPojo product) {
        ClientPojo client = clientService.getCheckClientById(product.getClientId());
        ProductData data = ConvertUtil.convert(product, ProductData.class);
        data.setClientName(client.getName());
        return data;
    }
}
