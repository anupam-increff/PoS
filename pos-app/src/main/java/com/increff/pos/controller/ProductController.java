package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductDto productDto;

    @PostMapping
    public void addProduct(@RequestBody @Valid ProductForm productForm) {
        productDto.addProduct(productForm);
    }

    @GetMapping
    public PaginatedResponse<ProductData> getAll(
            @RequestParam(required = false) String clientName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (!Objects.isNull(clientName) && !clientName.isEmpty()) {
            return productDto.getByClient(clientName, page, pageSize);
        }
        return productDto.getAll(page, pageSize);
    }

    @GetMapping("/search")
    public PaginatedResponse<ProductData> searchByBarcode(
            @RequestParam String barcode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return productDto.searchByBarcode(barcode, page, pageSize);
    }

    @GetMapping("/barcode/{barcode}")
    public ProductData getByBarcode(@PathVariable String barcode) {
        return productDto.getByBarcode(barcode);
    }


    @PostMapping(path = "/upload-tsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadProductMaster(@RequestParam("file") MultipartFile file) {
        productDto.uploadProductMasterByTsv(file);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ProductForm productForm) {
        productDto.update(id, productForm);
    }
}
