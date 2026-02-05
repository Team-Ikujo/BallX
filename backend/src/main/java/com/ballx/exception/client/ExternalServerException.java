package com.ballx.exception.client;

public class ExternalServerException extends RuntimeException {
	private final int status;
	private final String responseBody;

	public ExternalServerException(int status, String responseBody) {
		super("External API 5xx: " + status);
		this.status = status;
		this.responseBody = responseBody;
	}

	public int getStatus() { return status; }
	public String getResponseBody() { return responseBody; }
}
