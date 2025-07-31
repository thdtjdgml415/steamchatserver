package com.example.steamchatserver.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SteamUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String steamId) throws UsernameNotFoundException {
        // In a real application, you would load user details from a database
        // based on the steamId. For this example, we'll just create a new UserDetails object.
        return new SteamUserDetails(steamId);
    }
}
