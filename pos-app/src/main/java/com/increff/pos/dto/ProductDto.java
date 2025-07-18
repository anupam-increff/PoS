package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import com.increff.pos.util.TSVUtil;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.exception.ApiException;

@Component
public class ProductDto extends BaseDto {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    public void addProduct(@Valid ProductForm productForm) {
        productFlow.addProduct(productForm);
    }

    public PaginatedResponse<ProductData> getAll(int page, int pageSize) {
        List<ProductData> products = productFlow.getAllProducts(page, pageSize);
        long total = productFlow.countAllProducts();
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, int page, int pageSize) {
        List<ProductData> products = productFlow.getProductsByClient(clientName, page, pageSize);
        long total = productFlow.countProductsByClient(clientName);
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

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        try {
            // Step 1: Convert TSV to list of forms
            List<ProductForm> formList = TSVUtil.readFromTsv(file, ProductForm.class);
            
            if (formList.isEmpty()) {
                throw new ApiException("TSV file is empty or has no valid data");
            }
            
            List<String> successList = new ArrayList<>();
            List<String> failureList = new ArrayList<>();
            
            // Step 2: Process each form with validation and error handling
            for (int i = 0; i < formList.size(); i++) {
                ProductForm form = formList.get(i);
                try {
                    ValidationUtil.validate(form);
                    productFlow.addProduct(form);
                    successList.add("Row " + (i + 1) + ": Product added successfully");
                } catch (ApiException e) {
                    failureList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            // Step 3: Return response based on results
            if (!failureList.isEmpty()) {
                // Create error TSV for download
                String[] errorHeaders = {"Error"};
                List<String[]> errorRows = new ArrayList<>();
                for (String error : failureList) {
                    errorRows.add(new String[]{error});
                }
                byte[] errorTsv = TSVUtil.createTsvFromRows(errorRows, errorHeaders);
                String fileId = tsvDownloadService.storeTSVFile(errorTsv, "product_upload_errors.tsv");
                TSVUploadResponse resp = TSVUploadResponse.error(
                    "TSV processing completed with errors. " + successList.size() + " products added successfully.",
                    formList.size(),
                    failureList.size(),
                    failureList
                );
                resp.setDownloadUrl("/api/tsv/download/" + fileId);
                return resp;
            } else {
                return TSVUploadResponse.success(
                    "All " + successList.size() + " products added successfully",
                    successList.size()
                );
            }
            
        } catch (Exception e) {
            throw new ApiException("Failed to process TSV file: " + e.getMessage(), e);
        }
    }
}
