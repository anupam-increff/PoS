package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderService;
import com.increff.pos.util.ConvertUtil;
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

    public Integer placeOrder(@Valid OrderForm orderForm) {
        return orderFlow.placeOrder(orderForm);
    }

    public List<OrderData> getAll() {
        List<OrderPojo> orderPojos = orderService.getAll();
        return orderPojos.stream().
                map(orderPojo -> ConvertUtil.convert(orderPojo, OrderData.class)).
                collect(Collectors.toList());

    }

    public OrderData get(Integer id) {
        OrderPojo orderPojo = orderService.get(id);
        return ConvertUtil.convert(orderPojo, OrderData.class);
    }
}
