package com.increff.pos.dto;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class AuthDto extends AbstractDto {

    @Autowired
    private AuthService authService;

    public UserData signup(SignupForm form, HttpSession session) {
        checkValid(form);
        return authService.signup(form, session);
    }

    public UserData login(LoginForm form, HttpSession session) {
        checkValid(form);
        return authService.login(form, session);
    }

    public void logout(HttpSession session) {
        authService.logout(session);
    }

    public boolean isSessionValid(HttpSession session) {
        return authService.isSessionValid(session);
    }
} 