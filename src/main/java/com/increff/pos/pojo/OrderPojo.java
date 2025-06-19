package com.increff.pos.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter
public class OrderPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime time;

    private String invoicePath;
}