package com.increff.pos.controller;

import com.increff.pos.dto.InvoiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceDto invoiceDto;

    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer orderId) {
        return invoiceDto.downloadInvoice(orderId);
    }
    @GetMapping("/generate/{orderId}")
    public void generateInvoice(@PathVariable Integer orderId) throws Exception {
        invoiceDto.generateInvoice(orderId);
    }

}
