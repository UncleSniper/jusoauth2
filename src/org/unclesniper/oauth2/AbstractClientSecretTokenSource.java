package org.unclesniper.oauth2;

import java.io.IOException;

public abstract class AbstractClientSecretTokenSource extends AbstractHTTPTokenSource {

	private IOStringSource clientID;

	private IOStringSource clientSecret;

	public AbstractClientSecretTokenSource() {}

	public AbstractClientSecretTokenSource(IOStringSource clientID, IOStringSource clientSecret) {
		this.clientID = clientID;
		this.clientSecret = clientSecret;
	}

	public IOStringSource getClientID() {
		return clientID;
	}

	public void setClientID(IOStringSource clientID) {
		this.clientID = clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID == null ? null : () -> clientID;
	}

	public IOStringSource getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(IOStringSource clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret == null ? null : () -> clientSecret;
	}

	protected final String getString(IOStringSource source, String name)
			throws IOException, UnconfiguredPropertyException {
		if(source == null)
			throw new UnconfiguredPropertyException(name);
		String value = source.getString();
		if(value == null)
			throw new UnconfiguredPropertyException(name);
		return value;
	}

	@Override
	protected void setRequestParameters(ParameterSink sink) throws IOException, AuthorizationException {
		String theClientID = getString(clientID, "clientID");
		String theClientSecret = getString(clientSecret, "clientSecret");
		sink.putParameter("grant_type", "client_credentials");
		sink.putParameter("client_id", theClientID);
		sink.putParameter("client_secret", theClientSecret);
	}

}
