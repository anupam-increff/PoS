package com.increff.pos.dto;

import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ConvertUtil;
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
import java.util.Arrays;

@Component
public class ProductDto extends BaseDto {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    public void addProduct(@Valid ProductForm productForm) {
        ProductPojo productPojo= ConvertUtil.convert(productForm,ProductPojo.class);
        productFlow.addProduct(productPojo,productForm.getClientName());
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

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        try {
            // Step 1: Convert TSV to list of forms
            List<ProductForm> formList = TSVUtil.readFromTsv(file, ProductForm.class);
            List<String[]> rawRows = TSVUtil.readRawRows(file);
            
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
                    productFlow.addProduct(ConvertUtil.convert(form,ProductPojo.class),form.getClientName());
                    successList.add("Row " + (i + 1));
                } catch (ApiException e) {
                    // Combine original row values + error message
                    String[] row = Arrays.copyOf(rawRows.get(i), rawRows.get(i).length + 1);
                    row[row.length - 1] = e.getMessage();
                    failureList.add(String.join("\t", row));
                }
            }
            
            // Step 3: Return response based on results
            if (!failureList.isEmpty()) {
                // Create error TSV for download
                String[] errorHeaders = {"Barcode","ClientName","Name","MRP","Error"};
                List<String[]> errorRows = failureList.stream().map(s->s.split("\t",-1)).collect(java.util.stream.Collectors.toList());
                byte[] errorTsv = TSVUtil.createTsvFromRows(errorRows, errorHeaders);
                String fileId = tsvDownloadService.storeTSVFile(errorTsv, "product_upload_errors.tsv");
                TSVUploadResponse resp = TSVUploadResponse.error(
                    "TSV processing completed with errors. " + successList.size() + " products added successfully.",
                    formList.size(),
                    failureList.size(),
                    failureList
                );
                resp.setDownloadUrl("/tsv/download/" + fileId);
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
    public void update(Integer id, @Valid ProductForm productForm) {
        productFlow.updateProduct(id, productForm);
    }
}
