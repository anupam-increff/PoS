package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderDto orderDto;

    @GetMapping
    public List<OrderData> getAll() {
        return orderDto.getAll();
    }

    @GetMapping("/{id}")
    public List<OrderItemData> getItemsByOrderId(@PathVariable Integer id) {
        return orderDto.getItemsByOrderId(id);
    }

    @PostMapping
    public Integer placeOrder(@RequestBody @Valid OrderForm form) {
        return orderDto.placeOrder(form);
    }
}