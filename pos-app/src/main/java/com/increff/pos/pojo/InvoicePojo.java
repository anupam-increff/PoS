package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "invoices", uniqueConstraints = @UniqueConstraint(columnNames = "orderId"))
@Getter
@Setter
public class InvoicePojo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer orderId;

    @Column(nullable = false, length = 500)
    private String filePath;
} 