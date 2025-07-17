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

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column( nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Double mrp;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
