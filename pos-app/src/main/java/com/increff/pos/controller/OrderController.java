package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Api(tags = "Order Management")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation("Get all orders")
    @GetMapping
    public PaginatedResponse<OrderData> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderDto.getAll(page, size);
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
    @GetMapping("/search")
    public PaginatedResponse<OrderData> searchOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean invoiceGenerated,
            @RequestParam(required = false) String query,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return orderDto.searchOrders(startDate, endDate, invoiceGenerated, query, page, size);
    }
}