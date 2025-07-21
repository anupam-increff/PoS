package com.increff.pos.dto;

import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Component
public class AuthDto {

    @Autowired
    private AuthService authService;

    public UserData signup(@Valid SignupForm form, HttpSession session) {
        return authService.signup(form, session);
    }

    public UserData login(@Valid LoginForm form, HttpSession session) {
        return authService.login(form, session);
    }

    public void logout(HttpSession session) {
        authService.logout(session);
    }

    public boolean isSessionValid(HttpSession session) {
        return authService.isSessionValid(session);
    }
} 