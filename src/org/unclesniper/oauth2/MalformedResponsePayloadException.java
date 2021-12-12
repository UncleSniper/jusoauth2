package org.unclesniper.oauth2;

public class MalformedResponsePayloadException extends AuthorizationException {

	public MalformedResponsePayloadException(String serverResponse, Throwable cause) {
		super("Malformed server response" + (cause == null || cause.getMessage() == null
				|| cause.getMessage().length() == 0 ? "" : ": " + cause.getMessage()), serverResponse, cause);
	}

}
