package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
        List<OrderPojo> orders = orderService.getAll();
        List<OrderData> list = new ArrayList<>();

        for (OrderPojo o : orders) {
            OrderData d = new OrderData();
            d.setId(o.getId());
            d.setTime(o.getTime());
            d.setInvoicePath(o.getInvoicePath());
            list.add(d);
        }
        return list;
    }

    public OrderData get(Integer id) {
        OrderPojo o = orderService.get(id);
        OrderData d = new OrderData();
        d.setId(o.getId());
        d.setTime(o.getTime());
        d.setInvoicePath(o.getInvoicePath());
        return d;
    }
}
