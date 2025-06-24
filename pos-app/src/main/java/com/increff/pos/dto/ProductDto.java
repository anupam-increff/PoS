package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
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

    public ProductData getById(Integer id) {
        return ConvertUtil.convert(service.get(id), ProductData.class);
    }

    public void update(Integer id, @Valid ProductForm f) {
//        if(id!=f.getClientId()){
//            throw new ApiException("Invalid Product Id submitted") ;
//        }
        ProductPojo p = ConvertUtil.convert(f, ProductPojo.class);
        service.update(id, p);
    }
}