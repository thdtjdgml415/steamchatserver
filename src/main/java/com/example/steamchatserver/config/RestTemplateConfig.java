package com.example.steamchatserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 및 ObjectMapper 빈을 설정하는 구성 클래스입니다.
 * 이 클래스는 외부 API 호출 및 JSON 데이터 처리에 필요한 유틸리티 빈을 제공합니다.
 */
@Configuration
public class RestTemplateConfig {
    /**
     * 외부 API 호출을 위한 RestTemplate 빈을 생성합니다.
     * @return RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * JSON 데이터 처리를 위한 ObjectMapper 빈을 생성합니다.
     * @return ObjectMapper 객체
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

