package com.ballx.config.client;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import com.ballx.config.client.interceptor.ExternalLoggingInterceptor;
import com.ballx.config.client.interceptor.ExternalTraceInterceptor;
import com.ballx.config.properties.RestClientProperties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClient;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

@SpringJUnitConfig
@Import({RestClientConfig.class, RestClientIntegrationTest.TestConfig.class})
@EnableConfigurationProperties(RestClientProperties.class)
@TestPropertySource(properties = {
	"rest-client.connect-timeout-seconds=2",
	"rest-client.read-timeout-seconds=3"
})
class RestClientIntegrationTest {
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
	}

	@Autowired
	RestClient restClient;

	@Test
	void GET_요청_성공() {
		String response = restClient.get()
			.uri("https://jsonplaceholder.typicode.com/posts/1")
			.retrieve()
			.body(String.class);

		assertThat(response).isNotNull();
		assertThat(response).contains("userId");
	}

	@Test
	void GET_요청_실패_시_INTERNAL_SERVER_ERROR_발생() {
		assertThatThrownBy(() ->
			restClient.get()
				.uri("https://jsonplaceholder.typicode.com/posts/999999999")
				.retrieve()
				.body(String.class)
		)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.error()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
			});
	}
}
