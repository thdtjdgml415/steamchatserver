package com.example.steamchatserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
    "steam.openid.endpoint=https://steamcommunity.com/openid/login",
    "steam.return.url=http://localhost:3000/ranking",
    "jwt.secret=your-test-secret-key-that-is-long-enough-for-hmac-sha256",
    "jwt.expiration=3600000"
})
class SteamchatserverApplicationTests {

	@Test
	void contextLoads() {
	}

}
