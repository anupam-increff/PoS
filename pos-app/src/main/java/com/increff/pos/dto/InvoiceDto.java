package com.increff.pos.dto;

import com.increff.pos.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoiceDto {
    @Autowired
    InvoiceService invoiceService;
    public ResponseEntity<byte[]> downloadInvoice(Integer orderId){
        return  invoiceService.downloadInvoice(orderId);
    }
}
