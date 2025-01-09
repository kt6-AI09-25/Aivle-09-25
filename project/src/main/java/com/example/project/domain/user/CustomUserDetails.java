package com.example.project.domain.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    @Getter
    private final Integer state;
    @Getter
    private final LocalDateTime banEndTime;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, Integer state, LocalDateTime banEndTime, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.state = state;
        this.banEndTime = banEndTime;
        this.authorities = authorities;
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
        return username;
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
}
