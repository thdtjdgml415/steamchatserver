package com.example.steamchatserver.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SteamApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${steam.api.key}")
    private String steamApiKey;

    private static final String STEAM_API_BASE_URL = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s";

    public JsonNode getPlayerSummary(String steamId) throws IOException {
        String url = String.format(STEAM_API_BASE_URL, steamApiKey, steamId);
        String response = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(response);
        return root.path("response").path("players").get(0);
    }
}
