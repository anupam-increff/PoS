package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
@Entity
@Table(name = "order_item", uniqueConstraints = @UniqueConstraint(columnNames = {"orderId","productId"}))
@Getter
@Setter
public class OrderItemPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer orderId;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double sellingPrice;
}