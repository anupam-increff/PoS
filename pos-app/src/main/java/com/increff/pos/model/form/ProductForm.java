package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "Barcode must not be blank")
    @Size(max = 100, message = "Barcode must be at most 100 characters")
    private String barcode;

    @NotBlank(message = "Client name is required")
    @Size(max = 100, message = "Client name must be at most 100 characters")
    private String clientName;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be at most 100 characters")
    private String name;

    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.01", message = "MRP must be greater than zero")
    private Double mrp;

    @Size(max = 300, message = "Image URL must be at most 300 characters")
    private String imageUrl;
}
