package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.RowWrapper;
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
    private ProductService service;

    public void add(@Valid ProductForm f) {
        ProductPojo p = ConvertUtil.convert(f, ProductPojo.class);
        service.add(p);
    }

    public List<ProductData> getAll() {
        return service.getAll().stream()
                .map(p -> ConvertUtil.convert(p, ProductData.class))
                .collect(Collectors.toList());
    }
    public List<ProductData> getByClient(Integer clientId){
        return service.getByClient(clientId).stream()
                .map(p -> ConvertUtil.convert(p, ProductData.class))
                .collect(Collectors.toList());
    }

    public ProductData getById(Integer id) {
        return ConvertUtil.convert(service.get(id), ProductData.class);
    }
    public ProductData getByBarcode(String barcode) {
        return ConvertUtil.convert(service.getByBarcode(barcode), ProductData.class);
    }

    public void uploadProductMasterByTsv(MultipartFile file) {
        List<ProductForm> forms = TSVUtil.readFromTsv(file, ProductForm.class);

        List<String> errors = new ArrayList<>();
        int rowNum = 1;

        for (ProductForm form : forms) {
            try {
                // Call internal validated add/update
                add(form);
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

    public void update(Integer id, @Valid ProductForm f) {
        ProductPojo p = ConvertUtil.convert(f, ProductPojo.class);
        service.update(id, p);
    }
}