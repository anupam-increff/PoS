package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDto {

    @Autowired
    private ProductFlow productFlow;

    public void addProduct(@Valid ProductForm productForm) {
        ProductPojo productPojo = ConvertUtil.convert(productForm,ProductPojo.class);
        productFlow.addProduct(productPojo);
    }

    public List<ProductData> getAll() {
        return productFlow.getAllProducts();
    }
    public List<ProductData> getByClient(String clientName){
        return productFlow.getProductsByClient(clientName);
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
                // Call internal validated addProduct/update
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
        ProductPojo productPojo = ConvertUtil.convert(productForm,ProductPojo.class);
        productFlow.updateProduct(id, productPojo);
    }
}