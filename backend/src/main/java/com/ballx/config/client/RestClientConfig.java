package com.ballx.config.client;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.ballx.config.client.interceptor.ExternalLoggingInterceptor;
import com.ballx.config.client.interceptor.ExternalTraceInterceptor;
import com.ballx.config.properties.RestClientProperties;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestClientConfig {

	private final RestClientProperties props;
	private final ExternalTraceInterceptor traceInterceptor;
	private final ExternalLoggingInterceptor loggingInterceptor;
	private static final String TRACE_HEADER = "X-Trace-Id";

	@Bean
	public RestClient restClient(RestClient.Builder builder) {
		HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(props.connectTimeoutSeconds()))
			.build();

		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(Duration.ofSeconds(props.readTimeoutSeconds()));

		return builder
			.requestFactory(requestFactory)
			.requestInterceptor(traceInterceptor)
			.requestInterceptor(loggingInterceptor)
			.defaultStatusHandler(
				HttpStatusCode::isError,
				(request, response) -> handleError(request, response)
			)
			.build();
	}

	private void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
		int status = response.getStatusCode().value();
		String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
		String traceId = request.getHeaders().getFirst(TRACE_HEADER);

		log.error(
			"[External API Error] traceId={}, method={}, uri={}, status={}, body={}",
			traceId,
			request.getMethod(),
			request.getURI(),
			status,
			body
		);

		throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}
