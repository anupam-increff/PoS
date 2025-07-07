package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    public PaginatedResponse<OrderData> searchOrders(String startDateStr, String endDateStr, Boolean invoiceGenerated, int page, int size) {
        LocalDate startDate = (startDateStr == null) ? LocalDate.of(1970, 1, 1) : LocalDate.parse(startDateStr);
        LocalDate endDate = (endDateStr == null) ? LocalDate.now() : LocalDate.parse(endDateStr);
        return orderFlow.searchOrders(startDate, endDate, invoiceGenerated, page, size);
    }
}
