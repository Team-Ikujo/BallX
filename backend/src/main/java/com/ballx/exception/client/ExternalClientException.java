package com.ballx.exception.client;

public class ExternalClientException extends RuntimeException {
	private final int status;
	private final String responseBody;

	public ExternalClientException(int status, String responseBody) {
		super("External API 4xx: " + status);
		this.status = status;
		this.responseBody = responseBody;
	}

	public int getStatus() { return status; }
	public String getResponseBody() { return responseBody; }
}
