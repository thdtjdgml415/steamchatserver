package com.example.steamchatserver.controller;

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

import java.io.IOException;
import java.util.List;

/**
 * Steam OpenID를 이용한 로그인 처리를 담당하는 컨트롤러입니다.
 */
@Controller
@RequestMapping("/auth/steam")
public class SteamLoginController {

    private final String steamOpenIdEndpoint;
    private final String steamReturnUrl;
    private final ConsumerManager manager;

    public SteamLoginController(@Value("${steam.openid.endpoint}") String steamOpenIdEndpoint, 
                              @Value("${steam.return.url}") String steamReturnUrl) throws OpenIDException {
        this.steamOpenIdEndpoint = steamOpenIdEndpoint;
        this.steamReturnUrl = steamReturnUrl;
        this.manager = new ConsumerManager();
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
    public String steamCallback(HttpServletRequest request, HttpSession session) throws OpenIDException {
        // 세션에서 DiscoveryInformation 가져오기
        DiscoveryInformation discovered = (DiscoveryInformation) session.getAttribute("discovered");

        if (discovered == null) {
            // 세션에 DiscoveryInformation이 없으면 오류 처리
            return "Steam Login Failed: No discovery information in session!";
        }

        ParameterList response = new ParameterList(request.getParameterMap());
        StringBuffer receivingURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            receivingURL.append("?").append(queryString);
        }
        VerificationResult verification = manager.verify(receivingURL.toString(), response, discovered);
        Identifier verifiedId = verification.getVerifiedId();
        if (verifiedId != null) {
            // 로그인 성공
            String steamId = verifiedId.getIdentifier();
            // steamId를 사용하여 사용자 정보를 조회하거나 세션을 생성하는 로직 추가
            return "Steam Login Success! Steam ID: " + steamId;
        } else {
            // 로그인 실패
            return "Steam Login Failed!";
        }
    }
}