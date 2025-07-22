package com.increff.pos.service;

import com.increff.invoice.InvoiceGenerator;
import com.increff.invoice.model.OrderData;
import com.increff.invoice.model.OrderItemData;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.InvoiceStatus;
import com.increff.pos.pojo.InvoicePojo;
import com.increff.pos.dao.InvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Service
@Transactional(rollbackFor = ApiException.class)
public class InvoiceService {

    @Autowired
    private InvoiceDao invoiceDao;

    public boolean getInvoiceStatus(Integer orderId) {
        InvoicePojo invoice = invoiceDao.getByOrderId(orderId);
        return invoice != null;
    }

    public byte[] downloadInvoice(Integer orderId) {
        InvoicePojo invoice = invoiceDao.getByOrderId(orderId);
        if (invoice == null) {
            throw new ApiException("Invoice not generated for order id: " + orderId);
        }
        try {
            return Files.readAllBytes(Paths.get(invoice.getFilePath()));
        } catch (IOException e) {
            throw new ApiException("Invoice could not be read: " + e.getMessage());
        }
    }

    public void createInvoice(Integer orderId, String invoicePath, OrderData orderData, List<OrderItemData> itemDataList) {
        // Check if invoice already exists
        InvoicePojo existingInvoice = invoiceDao.getByOrderId(orderId);
        if (existingInvoice != null) {
            throw new ApiException("Invoice already exists for order ID: " + orderId);
        }

        // Generate PDF document
        byte[] pdfBytes = generatePdfDocument(orderData, itemDataList);
        saveInvoiceToFile(invoicePath, pdfBytes);

        // Persist invoice record
        InvoicePojo invoice = new InvoicePojo();
        invoice.setOrderId(orderId);
        invoice.setFilePath(invoicePath);
        invoice.setStatus(InvoiceStatus.GENERATED);
        invoiceDao.insert(invoice);
    }

    private byte[] generatePdfDocument(OrderData orderData, List<OrderItemData> itemDataList) {
        try {
            String base64Pdf = InvoiceGenerator.generate(orderData, itemDataList);
            return Base64.getDecoder().decode(base64Pdf);
        } catch (IllegalArgumentException | TransformerException | org.apache.fop.apps.FOPException e) {
            throw new ApiException("Failed during PDF generation: " + e.getMessage());
        }
    }

    private void saveInvoiceToFile(String invoicePath, byte[] pdfBytes) {
        try {
            Files.createDirectories(Paths.get("./invoices"));
            Files.write(Paths.get(invoicePath), pdfBytes);
        } catch (IOException e) {
            throw new ApiException("Failed to save invoice file: " + e.getMessage());
        }
    }
}
