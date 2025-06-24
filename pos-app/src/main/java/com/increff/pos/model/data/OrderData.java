package com.increff.pos.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderData {

    private Integer id;
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime time;
    private String invoicePath;
    private double total;

    public OrderData() {
    }

    public OrderData(Integer id, ZonedDateTime time, String invoicePath) {
        this.id = id;
        this.time = time;
        this.invoicePath = invoicePath;
    }
}
