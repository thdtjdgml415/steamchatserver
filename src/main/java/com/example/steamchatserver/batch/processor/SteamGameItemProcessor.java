package com.example.steamchatserver.batch.processor;

import com.example.steamchatserver.domain.SteamGame;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Steam API에서 가져온 게임 데이터를 {@link SteamGame} 엔티티로 변환하는 Spring Batch ItemProcessor입니다.
 */
@Component
public class SteamGameItemProcessor implements ItemProcessor<Integer, SteamGame> {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Steam 상점 API의 기본 URL을 주입받습니다.
    @Value("${steam.store.details-url}")
    private String baseUrl;

    // API 요청에 사용할 언어를 하드코딩합니다.
    private final String lang = "english";

    /**
     * SteamGameItemProcessor의 생성자입니다.
     * @param restTemplate Steam API 호출에 사용될 RestTemplate
     * @param objectMapper JSON 응답을 파싱하는 데 사용될 ObjectMapper
     */
    public SteamGameItemProcessor(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 주어진 appid를 사용하여 Steam API를 호출하고, 응답을 SteamGame 객체로 변환합니다.
     * @param appid 처리할 Steam 게임의 ID
     * @return 변환된 SteamGame 객체 또는 데이터가 없으면 null
     * @throws Exception API 호출 또는 JSON 파싱 중 오류 발생 시
     */
    @Override
    public SteamGame process(Integer appid) throws Exception {
        // Steam API 요청 URL을 구성합니다.
        String url = baseUrl + "?appids=" + appid + "&l=" + lang;
        // API를 호출하고 JSON 응답을 문자열로 가져옵니다.
        String response = restTemplate.getForObject(url, String.class);

        // JSON 응답을 파싱합니다.
        JsonNode root = objectMapper.readTree(response);
        // 응답에서 실제 게임 데이터가 포함된 노드를 찾습니다.
        JsonNode data = root.path(String.valueOf(appid)).path("data");

        // 데이터가 없으면 null을 반환합니다.
        if (data.isMissingNode()) {
            return null; // or handle error
        }

        // JSON 데이터를 SteamGame 객체로 변환하여 반환합니다.
        return createSteamGame(data);
    }

    /**
     * JsonNode에서 SteamGame 객체를 생성합니다.
     * @param data 게임 데이터가 포함된 JsonNode
     * @return 생성된 SteamGame 객체
     */
    private SteamGame createSteamGame(JsonNode data) {
        SteamGame game = new SteamGame();
        game.setAppid(data.path("steam_appid").asInt());
        game.setName(data.path("name").asText());
        game.setType(data.path("type").asText());
        game.setRequiredAge(data.path("required_age").asText());
        game.setIsFree(data.path("is_free").asBoolean());
        game.setDetailedDescription(data.path("detailed_description").asText());
        game.setAboutTheGame(data.path("about_the_game").asText());
        game.setShortDescription(data.path("short_description").asText());
        game.setSupportedLanguages(data.path("supported_languages").asText());
        game.setHeaderImageUrl(data.path("header_image").asText());
        game.setWebsiteUrl(data.path("website").asText());
        game.setReleaseDate(parseDate(data.path("release_date").path("date").asText()));
        game.setBackground(data.path("background").asText());
        return game;
    }

    /**
     * 날짜 문자열을 Date 객체로 파싱합니다.
     * @param dateStr 파싱할 날짜 문자열
     * @return 파싱된 Date 객체 또는 파싱 실패 시 null
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            // "MMM d, yyyy" 형식의 날짜를 파싱합니다. (예: "Jan 1, 2023")
            return new SimpleDateFormat("MMM d, yyyy").parse(dateStr);
        } catch (ParseException e) {
            // 파싱 오류 발생 시 예외를 처리하고 null을 반환합니다.
            // 실제 애플리케이션에서는 로깅 등을 통해 오류를 기록하는 것이 좋습니다.
            return null;
        }
    }
}
