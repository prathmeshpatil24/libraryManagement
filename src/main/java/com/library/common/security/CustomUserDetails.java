package com.library.common.security;

import com.library.user.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID= 1L;

    private Users users;

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        List<SimpleGrantedAuthority> grantedAuthorities = users.getRoles()
                .stream()
                .map(roleEntity ->
                        new SimpleGrantedAuthority(roleEntity.getRoleName())
                ).toList();

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return users.getIsActive();   // allow login only if active
    }

    public Long getUserId() {
        return users.getId();
    }

    //by default true setting
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
}
