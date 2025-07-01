package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemForm {
    @NotBlank(message = "Barcode must not be blank")
    private String barcode;
    @Min(value = 1,message = "Minimum quantity is least 1" )
    private Integer quantity;
    @NotNull(message = "MRP is required")
    @DecimalMin(value="0.01", message = "MRP must be greater than zero")
    private Double sellingPrice;
}
