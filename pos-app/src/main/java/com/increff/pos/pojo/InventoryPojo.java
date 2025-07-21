package com.increff.pos.pojo;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = "productId"))
@Getter 
@Setter
public class InventoryPojo extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer quantity;
}