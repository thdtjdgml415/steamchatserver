package com.example.steamchatserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SteamUserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String steamApiKey;

    public SteamUserService(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${steam.api.key}") String steamApiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.steamApiKey = steamApiKey;
    }

    public Map<String, String> getPlayerSummaries(String steamId64) {
        String url = String.format("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s", steamApiKey, steamId64);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode player = root.path("response").path("players").get(0);

            if (player != null && !player.isMissingNode()) {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("personaname", player.path("personaname").asText());
                userInfo.put("avatarfull", player.path("avatarfull").asText());
                return userInfo;
            }
        } catch (Exception e) {
            // 로깅 추가 권장
            System.err.println("Failed to get player summaries: " + e.getMessage());
        }
        return null;
    }
}
