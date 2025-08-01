package com.example.steamchatserver.controller;

import com.example.steamchatserver.domain.RefreshToken;
import com.example.steamchatserver.repository.RefreshTokenRepository;
import com.example.steamchatserver.service.SteamUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.http.ResponseCookie;

import com.example.steamchatserver.util.JwtTokenProvider;
import org.springframework.core.env.Environment;
import java.util.Arrays;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Steam OpenID를 이용한 로그인 처리를 담당하는 컨트롤러입니다.
 */
@Controller
@RequestMapping("/auth/steam")
public class SteamLoginController {

    private final String steamOpenIdEndpoint;
    private final String steamReturnUrl;
    private final ConsumerManager manager;
    private final JwtTokenProvider jwtTokenProvider;



// ... (다른 import 문들)

// ... (클래스 내부)
    private final Environment env;

    private final RefreshTokenRepository refreshTokenRepository;

    private final SteamUserService steamUserService;

    public SteamLoginController(@Value("${steam.openid.endpoint}") String steamOpenIdEndpoint,
                              @Value("${steam.return.url}") String steamReturnUrl,
                              JwtTokenProvider jwtTokenProvider,
                              Environment env,
                              RefreshTokenRepository refreshTokenRepository,
                              SteamUserService steamUserService) throws OpenIDException {
        this.steamOpenIdEndpoint = steamOpenIdEndpoint.trim();
        this.steamReturnUrl = steamReturnUrl.trim();
        this.manager = new ConsumerManager();
        this.manager.setAllowStateless(true);
        this.jwtTokenProvider = jwtTokenProvider;
        this.env = env;
        this.refreshTokenRepository = refreshTokenRepository;
        this.steamUserService = steamUserService;
    }

// ... (메소드들)

    private boolean isProduction() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    /**
     * Steam 로그인 프로세스를 시작합니다.
     * Steam OpenID 인증 페이지로 리다이렉트합니다.
     * @param response HttpServletResponse 객체
     * @param session HttpSession 객체
     * @throws IOException 리다이렉션 중 오류 발생 시
     * @throws OpenIDException OpenID 관련 오류 발생 시
     */
    @GetMapping("/login")
    public void steamLogin(HttpServletResponse response, HttpSession session) throws IOException, OpenIDException {
        List discoveries = manager.discover(steamOpenIdEndpoint);
        DiscoveryInformation discovered = manager.associate(discoveries);
        session.setAttribute("discovered", discovered);
        AuthRequest authReq = manager.authenticate(discovered, steamReturnUrl);
            response.sendRedirect(authReq.getDestinationUrl(true));
    }

    /**
     * Steam OpenID 인증 후 콜백을 처리합니다.
     * @param request HttpServletRequest 객체
     * @param session HttpSession 객체
     * @return 로그인 성공 또는 실패 메시지
     * @throws OpenIDException OpenID 관련 오류 발생 시
     */
    @GetMapping("/callback")
    public void steamCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        try {
            DiscoveryInformation discovered = (DiscoveryInformation) session.getAttribute("discovered");

            if (discovered == null) {
                throw new OpenIDException("No discovery information in session!");
            }

            ParameterList parameterList = new ParameterList(request.getParameterMap());
            StringBuffer receivingURL = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                receivingURL.append("?").append(queryString);
            }

            VerificationResult verification = manager.verify(receivingURL.toString(), parameterList, discovered);
            Identifier verifiedId = verification.getVerifiedId();

            if (verifiedId != null) {
                String steamIdUrl = verifiedId.getIdentifier();
                String steamId64 = steamIdUrl.substring(steamIdUrl.lastIndexOf("/") + 1);
                System.out.println(String.format("Steam ID: %s", steamId64));
                // Steam 사용자 정보 조회
                Map<String, String> userInfo = steamUserService.getPlayerSummaries(steamId64);

                // Access Token 및 Refresh Token 생성
                String accessToken = jwtTokenProvider.createAccessToken(steamId64, new HashMap<>(userInfo));
                String refreshToken = jwtTokenProvider.createRefreshToken(steamId64);

                // Refresh Token을 DB에 저장
                refreshTokenRepository.save(new RefreshToken(steamId64, refreshToken));

                // Access Token 쿠키 설정
                ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(isProduction())
                    .path("/")
                    .maxAge(60 * 60) // 1시간
                    .sameSite(isProduction() ? "None" : "Lax")
                    .build();

                // Refresh Token 쿠키 설정
                ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(isProduction())
                    .path("/auth/refresh") // 재발급 요청 시에만 사용
                    .maxAge(60 * 60 * 24 * 7) // 7일
                    .sameSite(isProduction() ? "None" : "Lax")
                    .build();

                // 응답 헤더에 쿠키 추가
                response.addHeader("Set-Cookie", accessTokenCookie.toString());
                response.addHeader("Set-Cookie", refreshTokenCookie.toString());

                // 성공 시 프론트엔드 메인 페이지로 리다이렉트
                response.sendRedirect("http://localhost:3000/ranking");
            } else {
                // 검증 실패
                response.sendRedirect("http://localhost:3000/login-failure?error=verification_failed");
            }
        } catch (OpenIDException e) {
            // OpenID 관련 예외 발생 시
            response.sendRedirect("http://localhost:3000/login-failure?error=openid_exception");
        }
    }
}