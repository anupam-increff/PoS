package com.increff.pos.dto;

import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Component
public class AuthDto {

    @Autowired
    private AuthService authService;

    public LoginData login(@Valid LoginForm form, HttpSession session) {
        return authService.login(form, session);
    }

    public void logout(HttpSession session) {
        authService.logout(session);
    }

    public boolean isSessionValid(HttpSession session) {
        return authService.isSessionValid(session);
    }
} 