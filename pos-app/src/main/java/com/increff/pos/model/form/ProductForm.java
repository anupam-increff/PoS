package com.increff.pos.model.form;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;

@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "Barcode must not be blank")
    private String barcode;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "MRP is required")
    @DecimalMin(value="0.01", message = "MRP must be greater than zero")
    private Double mrp;

    private String imageUrl;
}
