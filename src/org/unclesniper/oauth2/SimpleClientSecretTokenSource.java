package org.unclesniper.oauth2;

public class SimpleClientSecretTokenSource extends AbstractClientSecretTokenSource {

	private String endpointURL;

	public SimpleClientSecretTokenSource() {}

	public SimpleClientSecretTokenSource(String endpointURL) {
		this.endpointURL = endpointURL;
	}

	@Override
	public String getEndpointURL() {
		return endpointURL;
	}

	public void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}

}
