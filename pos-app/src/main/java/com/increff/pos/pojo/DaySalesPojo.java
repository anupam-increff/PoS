package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pos_day_sales")
@Getter
@Setter
public class DaySalesPojo {
    @Id
    private LocalDate date;

    private int invoicedOrdersCount;
    private int invoicedItemsCount;
    private double totalRevenue;
}
