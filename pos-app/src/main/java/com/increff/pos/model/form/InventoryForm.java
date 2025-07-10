package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InventoryForm {
    @NotBlank(message = "Barcode must not be blank")
    private String barcode;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Minimum quantity is at least 1")
    private Integer quantity;
}