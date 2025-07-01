package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @PostMapping()
    public void add(@RequestBody @Valid ProductForm form) {
        dto.add(form);
    }

    // Get all or by client
    @GetMapping
    public List<ProductData> getAll(@RequestParam(required = false) Integer clientId) {
        if (clientId != null) {
            return dto.getByClient(clientId);
        }
        return dto.getAll();
    }

    @PostMapping(path = "/upload-tsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadProductMaster(@RequestParam("file") MultipartFile file) {
        dto.uploadProductMasterByTsv(file);
    }

    @GetMapping("/barcode/{barcode}")
    public ProductData getByBarcode(@PathVariable String barcode) {
        return dto.getByBarcode(barcode);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ProductForm form) {
        dto.update(id, form);
    }
}
