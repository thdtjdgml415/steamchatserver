package com.example.steamchatserver.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Steam Spy API와 통신하여 데이터를 가져오는 클라이언트 클래스입니다.
 */
@Component
public class SteamSpyClient {
    private final RestTemplate rest;
    // Steam Spy API의 상위 100개 게임 URL을 주입받습니다.
    @Value("${steam.spy.top100-url}") private String url;

    /**
     * SteamSpyClient의 생성자입니다.
     * @param rest Steam Spy API 호출에 사용될 RestTemplate
     */
    public SteamSpyClient(RestTemplate rest) { this.rest = rest; }

    /**
     * Steam Spy API에서 상위 100개 게임 데이터를 가져옵니다.
     * @return API 응답 객체
     */
    public Object fetchTop100() {
        return rest.getForObject(url, Object.class);
    }
}