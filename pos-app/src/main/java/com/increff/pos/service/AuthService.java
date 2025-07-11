package com.increff.pos.service;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.util.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    public LoginData login(LoginForm form, HttpSession session) {
        Authentication auth = authenticateUser(form);
        setSecurityContext(auth, session);
        return buildLoginData(auth);
    }

    public void logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
    }

    public boolean isSessionValid(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    private Authentication authenticateUser(LoginForm form) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                form.getEmail(), form.getPassword()
        );
        return authManager.authenticate(token);
    }

    private void setSecurityContext(Authentication auth, HttpSession session) {
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    private LoginData buildLoginData(Authentication auth) {
        LoginData data = new LoginData();
        
        if (auth.getPrincipal() instanceof UserPrincipal) {
            populateFromUserPrincipal(data, (UserPrincipal) auth.getPrincipal());
        } else if (auth.getPrincipal() instanceof User) {
            populateFromUser(data, (User) auth.getPrincipal());
        } else {
            populateFromAuthentication(data, auth);
        }
        
        return data;
    }

    private void populateFromUserPrincipal(LoginData data, UserPrincipal principal) {
        data.setEmail(principal.getEmail());
        data.setRole(principal.getRole());
    }

    private void populateFromUser(LoginData data, User user) {
        data.setEmail(user.getUsername());
        data.setRole(user.getAuthorities().iterator().next().getAuthority());
    }

    private void populateFromAuthentication(LoginData data, Authentication auth) {
        data.setEmail(auth.getName());
        data.setRole(auth.getAuthorities().iterator().next().getAuthority());
    }
} 