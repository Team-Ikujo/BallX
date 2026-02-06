package com.ballx.config.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.ballx.config.client.interceptor.ExternalLoggingInterceptor;
import com.ballx.config.client.interceptor.ExternalTraceInterceptor;
import com.ballx.config.properties.RestClientProperties;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RestClientConfig {

	@Bean
	public RestClient externalRestClient(
		RestClientProperties props,
		ExternalTraceInterceptor traceInterceptor,
		ExternalLoggingInterceptor loggingInterceptor
	) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout((int) props.connectTimeoutMs());
		factory.setReadTimeout((int) props.readTimeoutMs());

		return RestClient.builder()
			.requestFactory(factory)
			.defaultStatusHandler(
				HttpStatusCode::isError,
				(request, response) -> handleError(request, response)
			)
			.requestInterceptor(traceInterceptor)
			.requestInterceptor(loggingInterceptor)
			.build();
	}

	private void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
		int status = response.getStatusCode().value();
		String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

		log.error(
			"[External API Error] {} {} -> {} | body={}",
			request.getMethod(),
			request.getURI(),
			status,
			body
		);

		throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}
