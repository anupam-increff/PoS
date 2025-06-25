
package com.increff.pos.controller;

import com.increff.pos.dto.InvoiceDto;
import com.increff.pos.exception.ApiException;

import com.increff.pos.pojo.OrderPojo;

import com.increff.pos.service.InvoiceService;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired private OrderService orderService;
    @Autowired
    private InvoiceDto invoiceDto;

    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer orderId) {
        return invoiceDto.downloadInvoice(orderId);
    }

}
