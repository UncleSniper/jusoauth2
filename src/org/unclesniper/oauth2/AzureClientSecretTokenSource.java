package org.unclesniper.oauth2;

import java.io.IOException;

public class AzureClientSecretTokenSource extends AbstractClientSecretTokenSource {

	private IOStringSource tenant;

	private IOStringSource resource;

	public AzureClientSecretTokenSource() {}

	public IOStringSource getTenant() {
		return tenant;
	}

	public void setTenant(IOStringSource tenant) {
		this.tenant = tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant == null ? null : () -> tenant;
	}

	public IOStringSource getResource() {
		return resource;
	}

	public void setResource(IOStringSource resource) {
		this.resource = resource;
	}

	public void setResource(String resource) {
		this.resource = resource == null ? null : () -> resource;
	}

	@Override
	public String getEndpointURL() throws IOException, AuthorizationException {
		StringBuilder builder = new StringBuilder();
		builder.append("https://login.microsoftonline.com/");
		builder.append(getString(tenant, "tenant"));
		builder.append("/oauth2/token");
		return builder.toString();
	}

	@Override
	protected void setRequestParameters(ParameterSink sink) throws IOException, AuthorizationException {
		super.setRequestParameters(sink);
		sink.putParameter("resource", getString(resource, "resource"));
	}

}
