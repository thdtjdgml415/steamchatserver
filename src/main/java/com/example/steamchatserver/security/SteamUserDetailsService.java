package com.example.steamchatserver.security;

import com.example.steamchatserver.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SteamUserDetailsService implements UserDetailsService {

    private final PlayerService playerService;

    @Override
    public UserDetails loadUserByUsername(String steamId) throws UsernameNotFoundException {
        try {
            playerService.saveOrUpdatePlayer(steamId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new SteamUserDetails(steamId);
    }
}
