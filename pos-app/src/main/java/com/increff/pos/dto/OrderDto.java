package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderSearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDto {

    @Autowired
    private OrderFlow orderFlow;

    public Integer placeOrder(OrderForm form) {
        return orderFlow.placeOrder(form);
    }

    public PaginatedResponse<OrderData> getAll(int page, int size) {
        return orderFlow.getAllPaginated(page, size);
    }

    public List<OrderItemData> getItemsByOrderId(Integer id) {
        return orderFlow.getOrderItemsByOrderId(id);
    }


    public PaginatedResponse<OrderData> searchOrdersByForm(OrderSearchForm form) {
        return orderFlow.searchOrders(form.getStartDate(), form.getEndDate(), form.getInvoiceGenerated(), form.getQuery(), form.getPage(), form.getSize());
    }
}
