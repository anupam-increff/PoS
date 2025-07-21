package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = "barcode")})
@Getter
@Setter
public class ProductPojo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String barcode;

    @Column(nullable = false)
    private Integer clientId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Double mrp;
    
    @Column
    private String imageUrl;
}
