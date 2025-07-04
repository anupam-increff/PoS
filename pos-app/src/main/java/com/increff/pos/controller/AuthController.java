package com.increff.pos.controller;

import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.data.LoginData;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthenticationManager authManager;

    public AuthController(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginData> login(@RequestBody LoginForm form, HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                form.getEmail(), form.getPassword()
        );

        Authentication auth = authManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        User principal = (User) auth.getPrincipal();

        LoginData data = new LoginData();
        data.setEmail(principal.getUsername());
        data.setRole(principal.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok(data);
    }
}
