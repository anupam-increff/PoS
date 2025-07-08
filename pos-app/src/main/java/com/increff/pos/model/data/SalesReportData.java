package com.increff.pos.model.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesReportData {

    private String client;
    private long quantity;
    private double revenue;

    public SalesReportData(String client, long quantity, double revenue) {
        this.client = client;
        this.quantity = quantity;
        this.revenue = revenue;
    }
}
