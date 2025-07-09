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
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    form.getEmail(), form.getPassword()
            );

            Authentication auth = authManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            LoginData data = new LoginData();
            
            // Handle both UserPrincipal and default User types
            if (auth.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
                data.setEmail(principal.getEmail());
                data.setRole(principal.getRole());
            } else if (auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                data.setEmail(user.getUsername());
                data.setRole(user.getAuthorities().iterator().next().getAuthority());
            } else {
                data.setEmail(auth.getName());
                data.setRole(auth.getAuthorities().iterator().next().getAuthority());
            }
            
            return data;
        } catch (Exception e) {
            throw new ApiException("Invalid credentials");
        }
    }

    public void logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
    }

    public boolean isSessionValid(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }
} 