package com.cateringmarketplace.module.auth.security;

import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.enums.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for Spring Security.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final String id;
    private final String userId;
    private final String email;
    private final String password;
    private final String userType;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.userType = user.getUserType().name();
        this.enabled = user.getStatus() == UserStatus.ACTIVE;
        this.accountNonLocked = !user.isAccountLocked();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

