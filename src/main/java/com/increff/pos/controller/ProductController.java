package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @PostMapping()
    public void add(@RequestBody @Valid ProductForm form){
        dto.add(form);
    }

    @GetMapping()
    public List<ProductData> getAll(
            @RequestParam(required = false) String client,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String name) {
        return dto.getAll();
    }
    @GetMapping("/{id}")
    public ProductData getById(@PathVariable int id){
            return dto.getById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ProductForm form){
        dto.update(id, form);
    }
}
