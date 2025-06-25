package com.increff.pos.service;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class InvoiceService {

    @Autowired
    private OrderFlow orderFlow;

    public byte[] downloadInvoice(Integer orderId) throws ApiException {
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
            return pdf;
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read or regenerated: " + e.getMessage());
        }
    }
}
