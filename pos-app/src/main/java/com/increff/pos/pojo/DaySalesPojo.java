package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "day_sales")
@Getter
@Setter
public class DaySalesPojo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime reportDate;

    @Column
    private Integer invoicedOrdersCount;

    @Column
    private Integer invoicedItemsCount;

    @Column
    private Double totalRevenue;
}
