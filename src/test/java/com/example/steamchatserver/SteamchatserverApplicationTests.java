package com.example.steamchatserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
    "steam.openid.endpoint=https://steamcommunity.com/openid/login",
    "steam.return.url=http://localhost:3000/ranking"
})
class SteamchatserverApplicationTests {

	@Test
	void contextLoads() {
	}

}
