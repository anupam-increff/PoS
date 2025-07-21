package com.increff.pos.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupForm {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    @Size(min = 6, max = 100, message = "Confirm Password must be between 6 and 100 characters")
    String confirmPassword;
}
