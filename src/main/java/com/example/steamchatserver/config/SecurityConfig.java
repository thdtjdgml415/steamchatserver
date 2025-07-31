package com.example.steamchatserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.example.steamchatserver.security.JwtAuthenticationFilter;
import com.example.steamchatserver.security.SteamUserDetailsService;
import com.example.steamchatserver.util.JwtTokenProvider;

/**
 * Spring Security 설정을 담당하는 구성 클래스입니다.
 * 웹 보안 규칙을 정의하고 HTTP 요청에 대한 접근 제어를 설정합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final SteamUserDetailsService steamUserDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, SteamUserDetailsService steamUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.steamUserDetailsService = steamUserDetailsService;
    }

    /**
     * SecurityFilterChain 빈을 정의하여 HTTP 보안 규칙을 구성합니다.
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/steam/**", "/auth/refresh").permitAll() // Steam 로그인 및 토큰 재발급 경로는 인증 없이 접근 허용
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 필요 시에만 세션 생성
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (개발 편의를 위해, 실제 서비스에서는 활성화 고려)
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, steamUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}