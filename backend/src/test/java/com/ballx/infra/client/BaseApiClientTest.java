package com.ballx.infra.client;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import com.ballx.config.client.RestClientConfig;
import com.ballx.config.client.interceptor.ExternalLoggingInterceptor;
import com.ballx.config.client.interceptor.ExternalTraceInterceptor;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

@Slf4j
@Import({
	RestClientConfig.class,
	ExternalLoggingInterceptor.class,
	ExternalTraceInterceptor.class
})
@TestPropertySource(properties = {
	"rest-client.connect-timeout-seconds=5",
	"rest-client.read-timeout-seconds=10"
})
@ActiveProfiles("test")
@SpringBootTest
class BaseApiClientTest {

	@Autowired
	RestClient restClient;

	TestApiClient testApiClient;

	static final String BASE_URL = "https://jsonplaceholder.typicode.com";

	static class TestApiClient extends BaseApiClient {
	}

	@BeforeEach
	void setup() {
		testApiClient = new TestApiClient();
	}

	@Test
	void restClient_get_요청_성공_테스트() {
		Map<String, String> query = Map.of("postId", "2");
		String result = testApiClient.get(
			restClient,
			BASE_URL + "/comments",
			null,
			query,
			String.class
		);
		assertNotNull(result);
		log.info("Test Result :: {}", result);
	}

	@Test
	void restClient_get_요청_실패_테스트() {

		CustomException exception = assertThrows(CustomException.class, () -> {
			testApiClient.get(
				restClient,
				BASE_URL + "/posts/99999999",
				String.class
			);
		});
		assertThat(exception.error()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	@Test
	void restClient_post_요청_성공_테스트() {
		Map<String, Object> body = Map.of(
			"title", "foo",
			"body", "bar",
			"userId", 1
		);
		String result = testApiClient.post(
			restClient,
			BASE_URL + "/posts",
			body,
			String.class
		);
		assertNotNull(result);
		log.info("result :: {}", result);
	}
}
