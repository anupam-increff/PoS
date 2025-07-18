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

    @Column(name = "placed_at", nullable = false)
    private ZonedDateTime placedAt;

    @Column(name = "invoice_generated", nullable = false)
    private Boolean invoiceGenerated = false;
    
    @Column(name = "total", nullable = false)
    private Double total;
}