package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class OrderForm {
    @NotNull(message = "Order cannot be empty")
    private List<OrderItemForm> items;
}
