package com.ballx.infra.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public abstract class BaseApiClient {

	protected HttpHeaders defaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
		return headers;
	}

	protected URI buildUri(String url, Map<String, ?> query) {
		if (query == null || query.isEmpty()) {
			return URI.create(url);
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

		query.forEach((k, v) -> {
			if (v != null)
				builder.queryParam(k, v);
		});

		return builder.build(true).toUri();
	}

	protected <T> T get(
		RestClient restClient,
		String url,
		Class<T> responseType
	) {
		return get(restClient, url, null, null, responseType);
	}

	protected <T> T get(
		RestClient restClient,
		String url,
		Map<String, String> headers,
		Class<T> responseType
	) {
		return get(restClient, url, headers, null, responseType);
	}

	protected <T> T get(
		RestClient restClient,
		String url,
		Map<String, String> headers,
		Map<String, ?> query,
		Class<T> responseType
	) {
		return restClient.get()
			.uri(buildUri(url, query))
			.headers(header -> {
				header.addAll(defaultHeaders());
				if (headers != null)
					headers.forEach(header::add);
			})
			.retrieve()
			.body(responseType);
	}

	protected <T> T post(
		RestClient restClient,
		String uri,
		Object body,
		Class<T> responseType
	) {
		return post(restClient, uri, null, body, responseType);
	}

	protected <T> T post(
		RestClient restClient,
		String uri,
		Map<String, String> headers,
		Object body,
		Class<T> responseType
	) {

		return restClient.post()
			.uri(buildUri(uri, Map.of()))
			.contentType(MediaType.APPLICATION_JSON)
			.headers(header -> {
				header.addAll(defaultHeaders());
				if (headers != null)
					headers.forEach(header::add);
			})
			.body(body)
			.retrieve()
			.body(responseType);
	}

	protected <T> T put(
		RestClient restClient,
		String url,
		Object body,
		Class<T> responseType
	) {
		return put(restClient, url, null, body, responseType);
	}

	protected <T> T put(
		RestClient restClient,
		String url,
		Map<String, String> headers,
		Object body,
		Class<T> responseType
	) {
		return restClient.put()
			.uri(url)
			.headers(header -> {
				header.addAll(defaultHeaders());
				if (headers != null)
					headers.forEach(header::add);
			})
			.body(body)
			.retrieve()
			.body(responseType);
	}

	protected <T> T delete(
		RestClient restClient,
		String url,
		Class<T> responseType
	) {
		return delete(restClient, url, null, null, responseType);
	}

	protected <T> T delete(
		RestClient restClient,
		String url,
		Map<String, String> headers,
		Class<T> responseType
	) {
		return delete(restClient, url, headers, null, responseType);
	}

	protected <T> T delete(
		RestClient restClient,
		String url,
		Map<String, String> headers,
		Map<String, ?> query,
		Class<T> responseType
	) {

		return restClient.delete()
			.uri(buildUri(url, query))
			.headers(header -> {
				header.addAll(defaultHeaders());
				if (headers != null)
					headers.forEach(header::add);
			})
			.retrieve()
			.body(responseType);
	}
}
