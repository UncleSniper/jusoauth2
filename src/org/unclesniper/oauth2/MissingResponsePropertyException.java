package org.unclesniper.oauth2;

public class MissingResponsePropertyException extends AuthorizationException {

	private final String property;

	public MissingResponsePropertyException(String property, String serverResponse) {
		super("Missing property in server response: " + property, serverResponse);
		this.property = property;
	}

}
