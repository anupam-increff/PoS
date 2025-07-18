package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("Add a new product")
    @PostMapping
    public void addProduct(@RequestBody @Valid ProductForm productForm) {
        productDto.addProduct(productForm);
    }

    @ApiOperation("Get all products with optional client filter")
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

    @ApiOperation("Search products by barcode")
    @GetMapping("/search")
    public PaginatedResponse<ProductData> searchByBarcode(
            @RequestParam String barcode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return productDto.searchByBarcode(barcode, page, pageSize);
    }

    @ApiOperation("Get product by barcode")
    @GetMapping("/barcode/{barcode}")
    public ProductData getByBarcode(@PathVariable String barcode) {
        return productDto.getByBarcode(barcode);
    }

    @ApiOperation("Upload product master data via TSV file (Supervisor only)")
    @PostMapping(path = "/upload-tsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TSVUploadResponse uploadProductMaster(@RequestParam("file") MultipartFile file) {
        return productDto.uploadProductMasterByTsv(file);
    }

    @ApiOperation("Update product by ID")
    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Integer id, @RequestBody @Valid ProductForm productForm) {
        productDto.update(id, productForm);
    }
}
