package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDto extends BaseDto {

    private static final int MAX_ROWS = 5000;

    @Autowired
    private ProductFlow productFlow;

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

    public void uploadProductMasterByTsv(MultipartFile file) {
        List<ProductForm> forms = TSVUtil.readFromTsv(file, ProductForm.class);
        
        if (forms.size() > MAX_ROWS) {
            throw new ApiException("Maximum " + MAX_ROWS + " rows allowed. Found: " + forms.size());
        }
        
        List<String> errors = new ArrayList<>();
        List<ProductForm> errorRows = new ArrayList<>();

        int rowNum = 1;
        for (ProductForm form : forms) {
            try {
                addProduct(form);
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
                errorRows.add(form);
            } catch (Exception e) {
                errors.add("Row " + rowNum + ": Unexpected error - " + e.getMessage());
                errorRows.add(form);
            }
            rowNum++;
        }
        
        if (!errors.isEmpty()) {
            // Generate error TSV file
            byte[] errorTsvBytes = TSVUtil.createTsvFromList(errorRows, ProductForm.class);
            String errorMessage = "Product TSV upload failed. " + errors.size() + " errors found:\n" + String.join("\n", errors);
            throw new ApiException(errorMessage + "\nError TSV file generated with " + errorRows.size() + " rows.");
        }
    }

    public void update(Integer id, @Valid ProductForm productForm) {
        productFlow.updateProduct(id, productForm);
    }
}
