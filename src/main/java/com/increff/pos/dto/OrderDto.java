package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderFlow orderFlow;

    public Integer placeOrder(@Valid OrderForm form) {
        return orderFlow.placeOrder(form);
    }
    public List<OrderData> getAll() {
        return orderService.getAll().stream().map(o ->
                OrderData.builder()
                        .id(o.getId())
                        .time(o.getTime())
                        .invoicePath(o.getInvoicePath())
                        .build()
        ).collect(Collectors.toList());
    }

    public OrderData get(Integer id) {
        OrderPojo o = orderService.get(id);
        return OrderData.builder()
                .id(o.getId())
                .time(o.getTime())
                .invoicePath(o.getInvoicePath())
                .build();
    }

}