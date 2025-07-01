package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InventoryForm {
    @NotNull(message = "Barcode must not be blank")
    private String barcode;

    @Min(value = 1,message = "Minimum quantity is least 1" )
    private Integer quantity;
}