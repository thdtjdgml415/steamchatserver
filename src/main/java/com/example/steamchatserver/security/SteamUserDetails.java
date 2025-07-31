package com.example.steamchatserver.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class SteamUserDetails implements UserDetails {

    private final String steamId;

    public SteamUserDetails(String steamId) {
        this.steamId = steamId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For simplicity, we're not assigning any specific roles/authorities here.
        // In a real application, you might assign roles based on user data.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null; // Passwords are not used with OpenID/JWT authentication
    }

    @Override
    public String getUsername() {
        return steamId;
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

    public String getSteamId() {
        return steamId;
    }
}
