package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Component
public class ProductDto extends BaseDto {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    public void addProduct(@Valid ProductForm productForm) {
        ProductPojo productPojo = ConvertUtil.convert(productForm, ProductPojo.class);
        productFlow.addProduct(productPojo, productForm.getClientName());
    }

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        String[] errorHeaders = {"Barcode", "ClientName", "Name", "MRP", "Error"};
        String errorFileName = "product_upload_errors.tsv";
        final int maxRows = 5000;
        String successMessage = "products added successfully";

        return TSVUploadUtil.processTSVUpload(
            file, ProductForm.class, errorHeaders, errorFileName, maxRows,
            form -> {
                ProductPojo productPojo = ConvertUtil.convert(form, ProductPojo.class);
                productFlow.addProduct(productPojo, form.getClientName());
            },
            successMessage, tsvDownloadService
        );
    }

    public PaginatedResponse<ProductData> getAll(int page, int pageSize) {
        List<ProductData> products = productFlow.getAllProducts(page, pageSize);
        long total = productFlow.countAllProducts();
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, int page, int pageSize) {
        List<ProductData> products = productFlow.getProductsByAClient(clientName, page, pageSize);
        long total = productFlow.countProductsByAClient(clientName);
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> searchByBarcode(String barcode, int page, int pageSize) {
        List<ProductData> products = productFlow.searchProductsByBarcode(barcode, page, pageSize);
        long total = productFlow.countSearchByBarcode(barcode);
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public ProductData getByBarcode(String barcode) {
        return productFlow.getProductByBarcode(barcode);
    }

    public void update(Integer id, @Valid ProductForm productForm) {
        productFlow.updateProduct(id, productForm);
    }
}
