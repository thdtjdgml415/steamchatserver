package com.example.steamchatserver.controller;

import com.example.steamchatserver.domain.RefreshToken;
import com.example.steamchatserver.repository.RefreshTokenRepository;
import com.example.steamchatserver.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.steamchatserver.service.SteamUserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")


// ...
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SteamUserService steamUserService;

    public TokenController(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository, SteamUserService steamUserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.steamUserService = steamUserService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 Refresh Token 가져오기
        String refreshToken = getRefreshTokenFromCookies(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }

        // 2. DB에서 Refresh Token 조회 및 검증
        String steamId = jwtTokenProvider.getSteamId(refreshToken);
        RefreshToken dbToken = refreshTokenRepository.findById(steamId)
                .orElse(null);

        if (dbToken == null || !dbToken.getToken().equals(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }

        // 3. 새로운 Access Token 생성
        Map<String, String> userInfo = steamUserService.getPlayerSummaries(steamId);
        String newAccessToken = jwtTokenProvider.createAccessToken(steamId, new HashMap<>(userInfo));

        // 4. 새로운 Access Token을 쿠키에 담아 응답
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false) // TODO: isProduction() 로직 추가 필요
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 Refresh Token 가져오기
        String refreshToken = getRefreshTokenFromCookies(request);

        // 2. Refresh Token이 존재하면 DB에서 삭제
        if (refreshToken != null) {
            String steamId = jwtTokenProvider.getSteamId(refreshToken);
            refreshTokenRepository.deleteById(steamId);
        }

        // 3. 클라이언트의 쿠키를 삭제하기 위해 만료된 쿠키를 생성하여 응답에 추가
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .path("/auth/refresh")
                .maxAge(0) // 즉시 만료
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok().body("Successfully logged out");
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
