package com.increff.pos.controller;

import com.increff.pos.dto.InvoiceDto;
import com.increff.pos.exception.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceDto invoiceDto;

    @ApiOperation("Download invoice for order")
    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer orderId) {
        try {
            return invoiceDto.downloadInvoice(orderId);
        } catch (Exception e) {
            throw new ApiException("Failed to download invoice: " + e.getMessage());
        }
    }

    @ApiOperation("Generate invoice for order")
    @PostMapping("/generate/{orderId}")
    public void generateInvoice(@PathVariable Integer orderId) {
        try {
            invoiceDto.generateInvoice(orderId);
        } catch (Exception e) {
            throw new ApiException("Failed to generate invoice: " + e.getMessage());
        }
    }
}
