package org.unclesniper.oauth2;

public class UnconfiguredPropertyException extends AuthorizationException {

	private final String unconfiguredProperty;

	public UnconfiguredPropertyException(String unconfiguredProperty) {
		super("Unconfigured property: " + unconfiguredProperty, null);
		this.unconfiguredProperty = unconfiguredProperty;
	}

	public String getUnconfiguredProperty() {
		return unconfiguredProperty;
	}

}
