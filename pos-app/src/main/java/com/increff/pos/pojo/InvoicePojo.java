package com.increff.pos.pojo;

import com.increff.pos.model.enums.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

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

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.NOT_GENERATED;

} 