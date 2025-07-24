package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InvoiceFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoiceDto {
    @Autowired
    InvoiceFlow invoiceFlow;
    
    public ResponseEntity<byte[]> downloadInvoiceById(Integer invoiceId){
        try {
            byte[] invoice = invoiceFlow.getInvoiceById(invoiceId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + invoiceId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(invoice);
        }
        catch (Exception e) {
            throw new ApiException("Failed to download invoice: " + e.getMessage());
        }
    }
    
    public Integer generateInvoice(Integer orderId) {
        return invoiceFlow.generateInvoice(orderId);
    }
}
