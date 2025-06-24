package com.increff.invoice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter
@NoArgsConstructor
public class OrderData {
    private Integer id;
    private ZonedDateTime time;
    private String invoicePath;
    private double total;

    // keep this if you like, but must be public:
    public OrderData(Integer id, ZonedDateTime time, String invoicePath) {
        this.id = id;
        this.time = time;
        this.invoicePath = invoicePath;
    }
}
