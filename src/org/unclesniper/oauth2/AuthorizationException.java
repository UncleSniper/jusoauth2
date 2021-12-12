package org.unclesniper.oauth2;

public class AuthorizationException extends Exception {

	private final String serverResponse;

	public AuthorizationException(String message, String serverResponse) {
		super(message);
		this.serverResponse = serverResponse;
	}

	public AuthorizationException(String message, String serverResponse, Throwable cause) {
		super(message, cause);
		this.serverResponse = serverResponse;
	}

	public String getServerResponse() {
		return serverResponse;
	}

}
