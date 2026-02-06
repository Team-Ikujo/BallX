package com.ballx.infra.client;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClient;

import com.ballx.config.client.RestClientConfig;
import com.ballx.config.client.interceptor.ExternalLoggingInterceptor;
import com.ballx.config.client.interceptor.ExternalTraceInterceptor;
import com.ballx.config.properties.RestClientProperties;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

@SpringJUnitConfig
@Import({RestClientConfig.class, BaseApiClientTest.TestConfig.class})
@EnableConfigurationProperties(RestClientProperties.class)
@TestPropertySource(properties = {
	"rest-client.connect-timeout-seconds=5",
	"rest-client.read-timeout-seconds=10"
})
class BaseApiClientTest {

	@Configuration
	static class TestConfig {
		@Bean
		RestClient.Builder restClientBuilder() {
			return RestClient.builder();
		}

		@Bean
		ExternalTraceInterceptor externalTraceInterceptor() {
			return new ExternalTraceInterceptor();
		}

		@Bean
		ExternalLoggingInterceptor externalLoggingInterceptor() {
			return new ExternalLoggingInterceptor();
		}

		@Bean
		TestApiClient testApiClient(RestClient restClient) {
			return new TestApiClient(restClient);
		}
	}

	static class TestApiClient extends BaseApiClient {
		private final RestClient restClient;

		TestApiClient(RestClient restClient) {
			this.restClient = restClient;
		}

		@Override
		protected String baseUrl() {
			return "https://jsonplaceholder.typicode.com";
		}

		String getPost1() {
			return get(restClient, "/posts/1", String.class);
		}

		String getMissingPostShouldFail() {
			return get(restClient, "/posts/999999999", String.class);
		}

		String getWithHeaders() {
			HttpHeaders headers = new HttpHeaders();
			headers.add("X-Debug", "true");
			return get(restClient, "/posts/1", headers, String.class);
		}

		String getWithHeadersAndQuery() {
			HttpHeaders headers = new HttpHeaders();
			headers.add("X-Debug", "true");

			return get(restClient, "/comments", headers, Map.of("postId", 1), String.class);
		}

		String createPost() {
			Map<String, Object> body = Map.of(
				"title", "foo",
				"body", "bar",
				"userId", 1
			);
			return post(restClient, "/posts", body, String.class);
		}

		String createPostWithHeaders() {
			HttpHeaders headers = new HttpHeaders();
			headers.add("X-Debug", "true");

			Map<String, Object> body = Map.of(
				"title", "hello",
				"body", "world",
				"userId", 1
			);
			return post(restClient, "/posts", headers, body, String.class);
		}
	}

	@Autowired
	TestApiClient api;

	@Test
	void GET_성공_posts_1() {
		String response = api.getPost1();
		assertThat(response).isNotBlank();
		assertThat(response).contains("\"id\": 1");
	}

	@Test
	void GET_실패_시_CustomException_INTERNAL_SERVER_ERROR() {
		assertThatThrownBy(api::getMissingPostShouldFail)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.error()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
			});
	}

	@Test
	void GET_header_추가_성공() {
		String response = api.getWithHeaders();
		assertThat(response).isNotBlank();
		assertThat(response).contains("\"id\": 1");
	}

	@Test
	void GET_header_query_추가_성공() {
		String response = api.getWithHeadersAndQuery();
		assertThat(response).isNotBlank();
		// comments 목록이 JSON으로 내려오므로 대충 배열 형태인지 확인
		assertThat(response.trim()).startsWith("[");
	}

	@Test
	void POST_성공_posts() {
		String response = api.createPost();
		assertThat(response).isNotBlank();
		// jsonplaceholder는 보통 생성된 id를 포함해서 응답
		assertThat(response).contains("\"id\":");
	}

	@Test
	void POST_header_추가_성공() {
		String response = api.createPostWithHeaders();
		assertThat(response).isNotBlank();
		assertThat(response).contains("\"id\":");
	}
}
