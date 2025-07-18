package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
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
        try {
            byte[] invoice = invoiceFlow.getInvoice(orderId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-" + orderId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(invoice);
        }
        catch (Exception e) {
            throw new ApiException("Failed to download invoice: " + e.getMessage());
        }
    }
    public void generateInvoice(Integer orderId) {
        invoiceFlow.generateInvoice(orderId);
    }
}
