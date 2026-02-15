package com.ballx.api;

import com.ballx.constants.ProviderType;
import com.ballx.constants.redis.RedisKey;
import com.ballx.infra.cache.RedisCache;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("State Get - POST /api/v1/auth/{provider}/state")
public class RequestStateTest {

	@Autowired
	RedisCache redisCache;

	@Autowired
	MockMvc mockMvc;

	static final String KEY_SEPARATOR = ":";

	@Test
	void state_생성_성공() throws Exception {
		ProviderType provider = ProviderType.NAVER;
		ResultActions actions = mockMvc.perform(
				get("/api/v1/auth/{provider}/state", provider))
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data.state").exists()
			);

		String response = actions.andReturn()
			.getResponse()
			.getContentAsString();

		String state = JsonPath.parse(response)
			.read("$.data.state", String.class);

		log.info("state :: {}", state);
		String keyParam = provider + KEY_SEPARATOR + state;
		boolean isExists = redisCache.hasKey(
			RedisKey.OAUTH_STATE.getKey(keyParam)
		);
		assertTrue(isExists);

	}

}
