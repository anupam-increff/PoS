package com.increff.pos.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.increff.pos.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderData {

    private Integer id;
    private ZonedDateTime placedAt;
    private OrderStatus orderStatus;
    private Integer invoiceId;

}
