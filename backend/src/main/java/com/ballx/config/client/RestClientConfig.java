package com.ballx.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.ballx.config.client.interceptor.ExternalLoggingInterceptor;
import com.ballx.config.client.interceptor.ExternalTraceInterceptor;
import com.ballx.config.properties.RestClientProperties;
import com.ballx.exception.client.handler.ExternalErrorHandler;

@Configuration
public class RestClientConfig {

	@Bean
	public ExternalErrorHandler externalErrorHandler() {
		return new ExternalErrorHandler();
	}

	@Bean
	public RestClient externalRestClient(
		RestClientProperties props,
		ExternalErrorHandler errorHandler,
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
				errorHandler
			)
			.requestInterceptor(traceInterceptor)
			.requestInterceptor(loggingInterceptor)
			.build();
	}
}
