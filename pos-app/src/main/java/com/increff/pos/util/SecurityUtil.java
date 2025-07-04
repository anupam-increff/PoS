package com.increff.pos.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static String getEmail() {
        return getPrincipal().getUsername();
    }

    public static String getRole() {
        return getPrincipal().getAuthorities().iterator().next().getAuthority();
    }
}
