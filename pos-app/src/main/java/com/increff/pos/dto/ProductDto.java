package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
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
        List<ErrorTSVData> errorDataList = new ArrayList<>();

        int rowNum = 1;
        for (ProductForm form : forms) {
            try {
                addProduct(form);
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
                errorDataList.add(new ErrorTSVData(form.getBarcode(), form.getName(), form.getClientName(), form.getMrp().toString(), e.getMessage()));
            } catch (Exception e) {
                errors.add("Row " + rowNum + ": Unexpected error - " + e.getMessage());
                errorDataList.add(new ErrorTSVData(form.getBarcode(), form.getName(), form.getClientName(), form.getMrp().toString(), e.getMessage()));
            }
            rowNum++;
        }
        
        if (!errors.isEmpty()) {
            // Generate error TSV file with error messages
            byte[] errorTsvBytes = createErrorTsv(errorDataList);
            String errorMessage = "Product TSV upload failed. " + errors.size() + " errors found:\n" + String.join("\n", errors);
            throw new ApiException(errorMessage + "\nError TSV file generated with " + errorDataList.size() + " rows.");
        }
    }

    public void update(Integer id, @Valid ProductForm productForm) {
        productFlow.updateProduct(id, productForm);
    }

    public static ProductForm convert(ProductPojo p) {
        ProductForm d = new ProductForm();
        d.setBarcode(p.getBarcode());
        d.setName(p.getName());
        d.setClientName(""); // This would need to be resolved from clientId
        d.setMrp(p.getMrp());
        d.setImageUrl(p.getImageUrl());
        return d;
    }

    public static ProductPojo convert(ProductForm d) {
        ProductPojo p = new ProductPojo();
        p.setBarcode(d.getBarcode());
        p.setName(d.getName());
        p.setClientId(0); // This would need to be resolved from clientName
        p.setMrp(d.getMrp());
        p.setImageUrl(d.getImageUrl());
        return p;
    }

    public static void copy(ProductForm d, ProductPojo p) {
        p.setBarcode(d.getBarcode());
        p.setName(d.getName());
        p.setMrp(d.getMrp());
        p.setImageUrl(d.getImageUrl());
    }

    public static PaginatedResponse<ProductForm> convertPaginated(List<ProductPojo> list, long total, int page, int size) {
        List<ProductForm> convertedList = new ArrayList<>();
        for (ProductPojo p : list) {
            convertedList.add(convert(p));
        }
        int totalPages = (int) Math.ceil((double) total / size);
        return new PaginatedResponse<>(convertedList, page, totalPages, total, size);
    }

    public static byte[] createErrorTsv(List<ErrorTSVData> errorDataList) {
        return TSVUtil.createErrorTsvFromList(errorDataList);
    }
}
