package com.increff.pos.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private ZonedDateTime time;
    private String invoicePath;
    private double total;

}
