package com.example.steamchatserver.controller;

import com.example.steamchatserver.domain.SteamGame;
import com.example.steamchatserver.repository.SteamGameRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Steam 게임 데이터를 제공하는 REST 컨트롤러입니다.
 * 데이터베이스에 저장된 게임 정보를 조회하는 API 엔드포인트를 정의합니다.
 */
@RestController
@RequestMapping("/api/games")
public class SteamGameController {

    private final SteamGameRepository steamGameRepository;

    /**
     * SteamGameController의 생성자입니다.
     * SteamGameRepository를 주입받아 게임 데이터에 접근합니다.
     * @param steamGameRepository SteamGame 엔티티의 데이터베이스 작업을 위한 리포지토리
     */
    public SteamGameController(SteamGameRepository steamGameRepository) {
        this.steamGameRepository = steamGameRepository;
    }

    /**
     * 모든 Steam 게임 데이터를 조회하는 GET 엔드포인트입니다.
     * @return 데이터베이스에 저장된 모든 SteamGame 객체의 리스트
     */
    @GetMapping
    public List<SteamGame> getAllGames() {
        return steamGameRepository.findAll();
    }
}