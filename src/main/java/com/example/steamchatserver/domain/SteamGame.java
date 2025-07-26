package com.example.steamchatserver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Steam 게임 정보를 나타내는 엔티티 클래스입니다.
 * JPA를 사용하여 데이터베이스의 'steam_game' 테이블에 매핑됩니다.
 * Lombok 어노테이션을 사용하여 보일러플레이트 코드를 줄였습니다.
 */
@NoArgsConstructor // 기본 생성자를 자동으로 생성합니다.
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 생성합니다.
@Getter // 모든 필드에 대한 getter 메서드를 자동으로 생성합니다.
@Setter // 모든 필드에 대한 setter 메서드를 자동으로 생성합니다.
@Entity // 이 클래스가 JPA 엔티티임을 나타냅니다.
@Table(name = "steam_game") // 이 엔티티가 매핑될 데이터베이스 테이블의 이름을 지정합니다.
public class SteamGame {

    @Id // 이 필드가 엔티티의 기본 키임을 나타냅니다.
    private Integer appid; // Steam 애플리케이션 ID
    private String name; // 게임 이름
    private String type; // 게임 타입 (예: game, dlc)
    private String requiredAge; // 요구 연령
    private Boolean isFree; // 무료 게임 여부
    @Column(columnDefinition = "TEXT") // 데이터베이스 컬럼 타입을 TEXT로 지정하여 긴 문자열을 저장할 수 있도록 합니다.
    private String detailedDescription; // 상세 설명
    @Column(columnDefinition = "TEXT")
    private String aboutTheGame; // 게임에 대한 설명
    @Column(columnDefinition = "TEXT")
    private String shortDescription; // 짧은 설명
    @Column(columnDefinition = "TEXT")
    private String supportedLanguages; // 지원 언어
    @Column(columnDefinition = "TEXT")
    private String headerImageUrl; // 헤더 이미지 URL
    @Column(columnDefinition = "TEXT")
    private String websiteUrl; // 웹사이트 URL
    private Date releaseDate; // 출시일
    @Column(columnDefinition = "TEXT")
    private String background; // 배경 이미지 URL
    private String updateAt; // 업데이트 시간 (문자열 형식)
    private Integer rate; // 평점
}
