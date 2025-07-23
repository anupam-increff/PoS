package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderSearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class OrderDto {

    @Autowired
    private OrderFlow orderFlow;

    public Integer placeOrder(@Valid OrderForm form) {
        return orderFlow.placeOrder(form);
    }

    public PaginatedResponse<OrderData> getAll(int page, int size) {
        return orderFlow.getAllPaginated(page, size);
    }

    public List<OrderItemData> getItemsByOrderId(Integer id) {
        return orderFlow.getOrderItemsByOrderId(id);
    }

    public PaginatedResponse<OrderData> searchOrders(ZonedDateTime start, ZonedDateTime end, String query, int page, int size){
        return orderFlow.searchOrders(start, end, null, query, page, size);
    }

    public PaginatedResponse<OrderData> searchOrdersByForm(OrderSearchForm form){
         return orderFlow.searchOrders(form.getStartDate(), form.getEndDate(), null, form.getQuery(), form.getPage(), form.getSize());
    }
}
