package com.example.steamchatserver.repository;

import com.example.steamchatserver.domain.SteamGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link SteamGame} 엔티티에 대한 데이터베이스 작업을 처리하는 Spring Data JPA 리포지토리 인터페이스입니다.
 * {@link JpaRepository}를 상속하여 기본적인 CRUD(생성, 읽기, 업데이트, 삭제) 및 페이징, 정렬 기능을 제공합니다.
 */
@Repository // 이 인터페이스가 Spring의 리포지토리 컴포넌트임을 나타냅니다.
public interface SteamGameRepository extends JpaRepository<SteamGame, Integer> {
    // JpaRepository<엔티티 타입, 엔티티의 ID 타입>을 상속받습니다.
    // 추가적인 쿼리 메서드가 필요하면 여기에 선언할 수 있습니다.
}
