package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product", uniqueConstraints = {@UniqueConstraint(columnNames = "barcode")})
@Getter
@Setter
public class ProductPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double mrp;

    @Column(name = "image_url")
    private String imageUrl;
}
