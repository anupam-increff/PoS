package com.increff.pos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class EmailAuthenticationService implements UserDetailsService {

    @Value("${auth.supervisor.emails}")
    private String supervisorEmails;
    
    @Value("${auth.default.password}")
    private String defaultPassword;
    
    private Set<String> supervisorEmailSet;
    
    @PostConstruct
    public void init() {
        Set<String> rawEmails = new HashSet<>(Arrays.asList(supervisorEmails.split(",")));
        supervisorEmailSet = new HashSet<>();
        // Trim whitespace from emails
        for (String email : rawEmails) {
            supervisorEmailSet.add(email.trim().toLowerCase());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email cannot be empty");
        }
        
        email = email.trim().toLowerCase();
        
        // Check if user is a supervisor
        String role = supervisorEmailSet.contains(email) ? "supervisor" : "operator";
        
        return User.builder()
                .username(email)
                .password(defaultPassword) // This will be encoded by the PasswordEncoder
                .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    public boolean isValidUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        email = email.trim().toLowerCase();
        
        // For now, accept any email with valid format
        // In production, you might want to restrict to specific domains
        return email.contains("@") && email.contains(".");
    }
    
    public String getUserRole(String email) {
        if (email == null) {
            return "operator";
        }
        
        email = email.trim().toLowerCase();
        return supervisorEmailSet.contains(email) ? "supervisor" : "operator";
    }
} 