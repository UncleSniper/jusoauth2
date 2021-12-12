package org.unclesniper.oauth2;

public class ResponsePayloadTooLongException extends AuthorizationException {

	public ResponsePayloadTooLongException(String serverResponse) {
		super("Server response was too long", serverResponse);
	}

}
