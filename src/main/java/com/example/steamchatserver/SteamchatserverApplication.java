package com.example.steamchatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot 애플리케이션의 메인 진입점 클래스입니다.
 * 이 클래스는 애플리케이션 컨텍스트를 부트스트랩하고 Spring Batch, JPA 리포지토리, 컴포넌트 스캔을 활성화합니다.
 */
@SpringBootApplication
// JPA 리포지토리를 스캔할 기본 패키지를 지정합니다.
@EnableJpaRepositories(basePackages = "com.example.steamchatserver.repository")
// Spring 컴포넌트를 스캔할 기본 패키지를 지정합니다.
@ComponentScan(basePackages = "com.example.steamchatserver")
public class SteamchatserverApplication {

	/**
	 * 애플리케이션의 메인 메서드입니다.
	 * SpringApplication.run()을 호출하여 Spring Boot 애플리케이션을 시작합니다.
	 * @param args 명령줄 인수
	 */
	public static void main(String[] args) {
		SpringApplication.run(SteamchatserverApplication.class, args);
	}

}
