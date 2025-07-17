package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "pos_day_sales")
@Getter
@Setter
public class DaySalesPojo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_date", nullable = false)
    private ZonedDateTime reportDate;

    @Column(name = "invoiced_orders_count")
    private Integer invoicedOrdersCount;

    @Column(name = "invoiced_items_count")
    private Integer invoicedItemsCount;

    @Column(name = "total_revenue")
    private Double totalRevenue;
}
