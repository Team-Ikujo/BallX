package com.ballx.api.auth;

import com.ballx.constants.redis.RedisKey;
import com.ballx.domain.dto.request.AuthCodeRequest;
import com.ballx.infra.cache.RedisCache;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("이메일 인증 코드 전송 - POST /api/v1/auth/mobile/code")
public class AuthSendAuthCodeTest {

	@Autowired
	RedisCache redisCache;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	final static String MOBILE = "01043053451";

	@Test
	void 인증번호_발송_성공_200_OK() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest(MOBILE);

		mockMvc.perform(
				post("/api/v1/auth/mobile/code")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			).andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			)
			.andExpect(status().isOk());

		String authCode = redisCache.get(
			RedisKey.AUTH_CODE.getKey(MOBILE),
			String.class
		);

		assertNotNull(authCode);
		log.info("authCode :: {}", authCode);
	}
}
