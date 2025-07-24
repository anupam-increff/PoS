package com.increff.pos.controller;

import com.increff.pos.dto.InvoiceDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceDto invoiceDto;

    @ApiOperation("Download invoice by invoice ID")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer invoiceId) {
        return invoiceDto.downloadInvoiceById(invoiceId);
    }

    @ApiOperation("Generate invoice for order and return invoice ID")
    @PostMapping("/generate/{orderId}")
    public Integer generateInvoice(@PathVariable Integer orderId) {
        return invoiceDto.generateInvoice(orderId);
    }
}
