package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderPojo extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_time", nullable = false)
    private ZonedDateTime time;
    
    @Column(name = "invoice_path", length = 500)
    private String invoicePath;
    
    @Column(name = "total", nullable = false)
    private Double total;
}