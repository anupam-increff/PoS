package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ClientService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDto extends BaseDto {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ProductService productService;

    @Autowired
    private ClientService clientService;

    public void addProduct(@Valid ProductForm form) {
        ProductPojo pojo = ConvertUtil.convert(form, ProductPojo.class);
        productFlow.addProduct(pojo, form.getClientName());
    }

    public PaginatedResponse<ProductData> getAll(int page, int pageSize) {
        List<ProductPojo> products = productService.getAll(page, pageSize);
        long totalProducts = productService.countAll();
        List<ProductData> productDataList = products.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(productDataList, page, (int) Math.ceil((double) totalProducts / pageSize), totalProducts, pageSize);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, int page, int pageSize) {
        List<ProductPojo> products = productFlow.getProductsByAClient(clientName, page, pageSize);
        long totalProducts = productFlow.countProductsByAClient(clientName);
        List<ProductData> productDataList = products.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(productDataList, page, (int) Math.ceil((double) totalProducts / pageSize), totalProducts, pageSize);
    }

    public PaginatedResponse<ProductData> searchByBarcode(String barcode, int page, int pageSize) {
        List<ProductPojo> products = productService.searchByBarcode(barcode, page, pageSize);
        long totalProducts = productService.countSearchByBarcode(barcode);
        List<ProductData> productDataList = products.stream().map(this::pojoToData).collect(Collectors.toList());
        return new PaginatedResponse<>(productDataList, page, (int) Math.ceil((double) totalProducts / pageSize), totalProducts, pageSize);
    }

    public ProductData getByBarcode(String barcode) {
        ProductPojo product = productService.getCheckProductByBarcode(barcode);
        return pojoToData(product);
    }

    public void update(Integer id, ProductForm form) {
        ProductPojo pojo = ConvertUtil.convert(form, ProductPojo.class);
        productFlow.updateProduct(id, pojo, form.getClientName());
    }

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        // TODO: Implement TSV upload logic
        return TSVUploadResponse.success("File uploaded successfully", 0);
    }

    private ProductData pojoToData(ProductPojo product) {
        ClientPojo client = clientService.getCheckClientById(product.getClientId());
        ProductData data = ConvertUtil.convert(product, ProductData.class);
        data.setClientName(client.getName());
        return data;
    }
}
