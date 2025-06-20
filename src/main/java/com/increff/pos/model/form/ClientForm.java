package com.increff.pos.model.form;

import lombok.Getter; import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter @Setter
public class ClientForm {
    @NotBlank
    private String name;
}
