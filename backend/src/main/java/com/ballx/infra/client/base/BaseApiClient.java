package com.ballx.infra.client.base;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
public abstract class BaseApiClient {

	private final RestClient restClient;

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

	public <T> T get(
		String url,
		Class<T> responseType
	) {
		return get(url, null, null, responseType);
	}

	protected <T> T get(
		String url,
		Map<String, String> headers,
		Class<T> responseType
	) {
		return get(url, headers, null, responseType);
	}

	protected <T> T get(
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
		String uri,
		Object body,
		Class<T> responseType
	) {
		return post(uri, null, body, null, responseType);
	}

	protected <T> T post(
		String uri,
		Object body,
		MediaType contentType,
		Class<T> responseType
	) {
		return post(uri, null, body, contentType, responseType);
	}

	protected <T> T post(
		String uri,
		Map<String, String> headers,
		Object body,
		MediaType contentType,
		Class<T> responseType
	) {
		return restClient.post()
			.uri(buildUri(uri, Map.of()))
			.contentType(
				contentType == null ? MediaType.APPLICATION_JSON : contentType
			)
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
		String url,
		Object body,
		Class<T> responseType
	) {
		return put(url, null, null, body, responseType);
	}

	protected <T> T put(
		String url,
		Map<String, String> headers,
		Object body,
		Class<T> responseType
	) {
		return put(url, headers, null, body, responseType);
	}

	protected <T> T put(
		String url,
		Map<String, String> headers,
		Map<String, ?> query,
		Object body,
		Class<T> responseType
	) {
		return restClient.put()
			.uri(buildUri(url, query))
			.headers(header -> {
				header.addAll(defaultHeaders());
				if (headers != null)
					headers.forEach(header::add);
			})
			.body(body)
			.retrieve()
			.body(responseType);
	}

	protected <T> T delete(String url, Class<T> responseType) {
		return delete(url, null, null, responseType);
	}

	protected <T> T delete(
		String url,
		Map<String, String> headers,
		Class<T> responseType
	) {
		return delete(url, headers, null, responseType);
	}

	protected <T> T delete(
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
