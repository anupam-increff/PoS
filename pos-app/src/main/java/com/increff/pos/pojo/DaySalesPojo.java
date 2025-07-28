package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "day_sales" ,uniqueConstraints = @UniqueConstraint(columnNames = "reportDate"))
@Getter
@Setter
public class DaySalesPojo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotNull(message = "Report date cannot be null")
    private ZonedDateTime reportDate;

    @Column
    @Min(value = 0, message = "Invoiced orders count must be positive or zero")
    private Integer invoicedOrdersCount;

    @Column
    @Min(value = 0, message = "Invoiced items count must be positive or zero")
    private Integer invoicedItemsCount;

    @Column
    @Min(value = 0, message = "Total revenue must be positive or zero")
    private Double totalRevenue;
}
