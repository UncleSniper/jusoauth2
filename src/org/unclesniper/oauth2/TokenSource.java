package org.unclesniper.oauth2;

import java.io.IOException;

public interface TokenSource {

	public static final long DEFAULT_USE_GRACE_PERIOD = 5000L;

	long getUseGracePeriod();

	void setUseGracePeriod(long period);

	String getToken() throws IOException, AuthorizationException;

}
