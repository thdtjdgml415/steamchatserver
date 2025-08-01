package com.example.steamchatserver.service;

import com.example.steamchatserver.client.SteamApiClient;
import com.example.steamchatserver.domain.Player;
import com.example.steamchatserver.repository.PlayerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final SteamApiClient steamApiClient;

    @Transactional
    public Player saveOrUpdatePlayer(String steamId) throws IOException {
        JsonNode playerJson = steamApiClient.getPlayerSummary(steamId);
        if (playerJson == null || playerJson.isEmpty()) {
            return null;
        }

        Player player = new Player();
        player.setSteamId(playerJson.path("steamid").asText());
        player.setPersonaName(playerJson.path("personaname").asText());
        player.setProfileUrl(playerJson.path("profileurl").asText());
        player.setAvatar(playerJson.path("avatar").asText());
        player.setAvatarMedium(playerJson.path("avatarmedium").asText());
        player.setAvatarFull(playerJson.path("avatarfull").asText());
        player.setPersonaState(playerJson.path("personastate").asInt());
        player.setCommunityVisibilityState(playerJson.path("communityvisibilitystate").asInt());
        player.setProfileState(playerJson.path("profilestate").asInt());
        player.setRealName(playerJson.path("realname").asText(null));
        player.setPrimaryClanId(playerJson.path("primaryclanid").asText(null));
        player.setTimeCreated(playerJson.path("timecreated").asLong(0));
        player.setLocCountryCode(playerJson.path("loccountrycode").asText(null));
        player.setLocStateCode(playerJson.path("locstatecode").asText(null));
        player.setLocCityId(playerJson.path("loccityid").asInt(0));

        return playerRepository.save(player);
    }

    @Transactional(readOnly = true)
    public Player getPlayer(String steamId) {
        return playerRepository.findById(steamId).orElse(null);
    }
}
