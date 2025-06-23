package com.increff.invoice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter @Builder
public class OrderData {
    private Integer id;
    private ZonedDateTime time;
    private String invoicePath;
}
