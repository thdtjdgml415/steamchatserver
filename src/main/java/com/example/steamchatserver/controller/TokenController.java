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

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenController(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
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
        String newAccessToken = jwtTokenProvider.createAccessToken(steamId);

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
