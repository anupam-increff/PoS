package com.increff.pos.service;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class InvoiceService {

    @Autowired
    private OrderFlow orderFlow;

    public ResponseEntity<byte[]> downloadInvoice(Integer orderId) throws ApiException {
        OrderPojo order = orderFlow.get(orderId);

        if (order == null) {
            throw new ApiException("Invoice not found for order: " + orderId);
        }
        if(order.getInvoicePath()==null){
            try {
                orderFlow.generateInvoice(orderId);
            }
            catch (Exception e){
                throw new ApiException("Failed to generate the invoice");
            }
        }

        try {
            byte[] pdf = Files.readAllBytes(Paths.get(order.getInvoicePath()));
            return  ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-" + orderId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read or regenerated: " + e.getMessage());
        }
    }
}
