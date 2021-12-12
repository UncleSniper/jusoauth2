package org.unclesniper.oauth2;

public class BadResponseStatusCodeException extends AuthorizationException {

	private final int statusCode;

	public BadResponseStatusCodeException(int statusCode, String serverResponse) {
		super("Bad response status code: " + statusCode, serverResponse);
		this.statusCode = statusCode;
	}

}
