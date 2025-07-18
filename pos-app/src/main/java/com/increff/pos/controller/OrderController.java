package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderSearchForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderDto orderDto;


    @ApiOperation("Get all orders")
    @GetMapping
    public PaginatedResponse<OrderData> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return orderDto.getAll(page, pageSize);
    }

    @ApiOperation("Get order items by order ID")
    @GetMapping("/{id}")
    public List<OrderItemData> getItemsByOrderId(@PathVariable Integer id) {
        return orderDto.getItemsByOrderId(id);
    }

    @ApiOperation("Place a new order")
    @PostMapping
    public Integer placeOrder(@RequestBody @Valid OrderForm form) {
        return orderDto.placeOrder(form);
    }

    @ApiOperation("Search orders with filters")
    @PostMapping("/search")
    public PaginatedResponse<OrderData> searchOrders(@RequestBody @Valid OrderSearchForm form) {
        return orderDto.searchOrdersByForm(form);
    }
}