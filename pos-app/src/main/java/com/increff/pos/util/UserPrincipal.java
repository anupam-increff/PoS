package com.increff.pos.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrincipal extends User {

    private String email;
    private String role;

    public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = username;
        this.role = authorities.iterator().hasNext() ? authorities.iterator().next().getAuthority() : null;
    }

    public UserPrincipal(String username, String password, boolean enabled, boolean accountNonExpired,
                        boolean credentialsNonExpired, boolean accountNonLocked,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.email = username;
        this.role = authorities.iterator().hasNext() ? authorities.iterator().next().getAuthority() : null;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isSupervisor() {
        return "supervisor".equals(role);
    }

    public boolean isOperator() {
        return "operator".equals(role);
    }
}
