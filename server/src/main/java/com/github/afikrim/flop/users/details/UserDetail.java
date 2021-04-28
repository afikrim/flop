package com.github.afikrim.flop.users.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.afikrim.flop.roles.Role;
import com.github.afikrim.flop.users.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetail implements UserDetails {

    private static final long serialVersionUID = 1L;

    private User user;

    public UserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> roles = new ArrayList<>(user.getRoles());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role: roles) {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getAccount().getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
