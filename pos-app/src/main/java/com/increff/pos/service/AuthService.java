package com.increff.pos.service;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;

@Service
public class AuthService {

    private static final String USER_PRINCIPAL = "userPrincipal";

    @Autowired
    private UserService userService;

    public LoginData signup(@Valid SignupForm form, HttpSession session) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new ApiException("Password and confirm password do not match");
        }

        // Create user using UserService
        UserPojo user = userService.signup(form.getEmail(), form.getPassword());

        // Create session and Spring Security context
        return createUserSession(user, session);
    }

    public LoginData login(@Valid LoginForm form, HttpSession session) {

        // Authenticate using UserService
        UserPojo user = userService.login(form.getEmail(), form.getPassword());

        // Create session and Spring Security context
        return createUserSession(user, session);
    }

    private LoginData createUserSession(UserPojo user, HttpSession session) {
        String role = user.getRole().toString().toLowerCase();
        String roleWithPrefix = "ROLE_" + role.toUpperCase();

        // Create session
        session.setAttribute(USER_PRINCIPAL, user.getEmail());
        session.setAttribute("userRole", role);

        // Set Spring Security context
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                Collections.singletonList(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        LoginData loginData = new LoginData();
        loginData.setEmail(user.getEmail());
        loginData.setRole(role);
        return loginData;
    }

    public void logout(HttpSession session) {
        session.removeAttribute(USER_PRINCIPAL);
        session.invalidate();
        SecurityContextHolder.clearContext();
    }

    public boolean isSessionValid(HttpSession session) {
        String email = (String) session.getAttribute(USER_PRINCIPAL);
        return email != null;
    }

} 