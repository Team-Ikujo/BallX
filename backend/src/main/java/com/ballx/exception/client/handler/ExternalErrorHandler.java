package com.ballx.exception.client.handler;

import com.ballx.exception.client.ExternalClientException;
import com.ballx.exception.client.ExternalServerException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExternalErrorHandler implements RestClient.ResponseSpec.ErrorHandler {
	@Override
	public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
		int status = response.getStatusCode().value();
		String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

		if (status >= 400 && status < 500) throw new ExternalClientException(status, body);
		if (status >= 500) throw new ExternalServerException(status, body);
	}
}
