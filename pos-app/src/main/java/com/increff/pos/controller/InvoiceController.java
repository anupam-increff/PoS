package com.increff.pos.controller;

import com.increff.pos.dto.InvoiceDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Invoice Management")
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceDto invoiceDto;

    @ApiOperation("Download invoice for order")
    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer orderId) {
        return invoiceDto.downloadInvoice(orderId);
    }
    
    @ApiOperation("Generate invoice for order")
    @GetMapping("/generate/{orderId}")
    public void generateInvoice(@PathVariable Integer orderId) throws Exception {
        invoiceDto.generateInvoice(orderId);
    }
}
