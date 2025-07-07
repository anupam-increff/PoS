package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDto {

    @Autowired
    private ProductFlow productFlow;

    public void addProduct(@Valid ProductForm productForm) {
        productFlow.addProduct(productForm);
    }


    public PaginatedResponse<ProductData> getAll(int page, int pageSize) {
        List<ProductPojo> pojos = productFlow.getAllProducts(page, pageSize);
        long total = productFlow.countAllProducts();
        List<ProductData> data = toDataList(pojos);
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, int page, int pageSize) {
        List<ProductPojo> pojos = productFlow.getProductsByClient(clientName, page, pageSize);
        long total = productFlow.countProductsByClient(clientName);
        List<ProductData> data = pojos.stream()
                .map(productFlow::productPojoToProductData)
                .collect(Collectors.toList());
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }


    public PaginatedResponse<ProductData> searchByBarcode(String barcode, int page, int pageSize) {
        List<ProductPojo> pojos = productFlow.searchProductsByBarcode(barcode, page, pageSize);
        long total = productFlow.countSearchByBarcode(barcode);
        List<ProductData> data = toDataList(pojos);
        return new PaginatedResponse<>(data, page, (int) Math.ceil((double) total / pageSize), total, pageSize);
    }

    public ProductData getByBarcode(String barcode) {
        return productFlow.getProductByBarcode(barcode);
    }

    public void uploadProductMasterByTsv(MultipartFile file) {
        List<ProductForm> forms = TSVUtil.readFromTsv(file, ProductForm.class);
        List<String> errors = new ArrayList<>();
        int rowNum = 1;
        for (ProductForm form : forms) {
            try {
                addProduct(form);
            } catch (ApiException e) {
                errors.add("Row " + rowNum + ": " + e.getMessage());
            } catch (Exception e) {
                errors.add("Row " + rowNum + ": Unexpected error - " + e.getMessage());
            }
            rowNum++;
        }
        if (!errors.isEmpty()) {
            throw new ApiException("TSV upload encountered issues:\n" + String.join("\n", errors));
        }
    }

    public void update(Integer id, @Valid ProductForm productForm) {
        productFlow.updateProduct(id, productForm);
    }

    private List<ProductData> toDataList(List<ProductPojo> pojos) {
        return pojos.stream().map(p -> productFlow.productPojoToProductData(p)).collect(Collectors.toList());
    }
}
