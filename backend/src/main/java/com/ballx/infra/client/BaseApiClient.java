package com.ballx.infra.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public abstract class BaseApiClient {

	protected abstract String baseUrl();

	protected HttpHeaders defaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
		return headers;
	}

	protected URI buildUri(String path, Map<String, ?> query) {
		UriComponentsBuilder b = UriComponentsBuilder
			.fromUriString(baseUrl())
			.path(path);

		if (query != null) {
			query.forEach((k, v) -> {
				if (v != null) b.queryParam(k, v);
			});
		}
		return b.build(true).toUri();
	}

	// --- GET ---
	protected <T> T get(
		RestClient restClient, String path, Class<T> responseType
	) {
		return restClient.get()
			.uri(buildUri(path, Map.of()))
			.headers(h -> h.addAll(defaultHeaders()))
			.retrieve()
			.body(responseType);
	}
	protected <T> T get(
		RestClient restClient, String path, HttpHeaders headers, Class<T> responseType
	) {
		HttpHeaders merged = new HttpHeaders();
		merged.addAll(defaultHeaders());
		merged.addAll(headers);

		return restClient.get()
			.uri(buildUri(path, Map.of()))
			.headers(h -> h.addAll(merged))
			.retrieve()
			.body(responseType);
	}

	protected <T> T get(
		RestClient restClient, String path,	HttpHeaders headers, Map<String, ?> query, Class<T> responseType
	) {
		HttpHeaders merged = new HttpHeaders();
		merged.addAll(defaultHeaders());
		merged.addAll(headers);

		return restClient.get()
			.uri(buildUri(path, query))
			.headers(h -> h.addAll(merged))
			.retrieve()
			.body(responseType);
	}

	// --- POST ---
	protected <T> T post(RestClient restClient, String path, Object body, Class<T> responseType) {
		return restClient.post()
			.uri(buildUri(path, null))
			.contentType(MediaType.APPLICATION_JSON)
			.headers(h -> h.addAll(defaultHeaders()))
			.body(body)
			.retrieve()
			.body(responseType);
	}

	// --- PUT ---
	protected <T> T put(RestClient restClient, String path, Object body, Class<T> responseType) {
		return restClient.put()
			.uri(buildUri(path, null))
			.contentType(MediaType.APPLICATION_JSON)
			.headers(h -> h.addAll(defaultHeaders()))
			.body(body)
			.retrieve()
			.body(responseType);
	}

	// --- DELETE ---
	protected <T> T delete(RestClient restClient, String path, Map<String, ?> query, Class<T> responseType) {
		return restClient.delete()
			.uri(buildUri(path, query))
			.headers(h -> h.addAll(defaultHeaders()))
			.retrieve()
			.body(responseType);
	}
}
