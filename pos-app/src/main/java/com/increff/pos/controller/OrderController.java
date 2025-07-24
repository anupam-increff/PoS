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
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

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
    public OrderData placeOrder(@RequestBody OrderForm form) {
        return orderDto.placeOrder(form);
    }

    @ApiOperation("Search orders with filters")
    @PostMapping("/search")
    public PaginatedResponse<OrderData> searchOrders(@RequestBody  OrderSearchForm form) {
        return orderDto.searchOrdersByForm(form);
    }

    @ApiOperation("Search orders with filters")
    @GetMapping("/search")
    public PaginatedResponse<OrderData> searchOrdersGet(
            @RequestParam(required = false) String query,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ZonedDateTime start = ZonedDateTime.parse(startDate.trim());
        ZonedDateTime end = ZonedDateTime.parse(endDate.trim());
        return orderDto.searchOrders(start, end, query, page, size);
    }
}