package com.increff.pos.dto;

import com.increff.pos.flow.InvoiceFlow;
import com.increff.pos.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoiceDto {
    @Autowired
    InvoiceFlow invoiceFlow;
    public ResponseEntity<byte[]> downloadInvoice(Integer orderId){
        byte[] invoice = invoiceFlow.getInvoice(orderId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(invoice);
    }
    public void generateInvoice(Integer orderId) throws Exception {
        invoiceFlow.generateInvoice(orderId);
    }
}
