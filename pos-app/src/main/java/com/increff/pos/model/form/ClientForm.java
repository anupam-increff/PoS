package com.increff.pos.model.form;

import lombok.Getter; import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter @Setter
public class ClientForm {
    @NotBlank(message = "Client name is required")
    @Size(min = 1, max = 100, message = "Client name must be between 1 and 100 characters")

    private String name;
}
