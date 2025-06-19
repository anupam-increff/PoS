package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderDto dto;

    @PostMapping
    public Integer placeOrder(@RequestBody @Valid OrderForm form) {
        return dto.placeOrder(form);
    }
}