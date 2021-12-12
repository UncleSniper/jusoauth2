package org.unclesniper.oauth2;

import java.io.IOException;

import static org.unclesniper.util.ArgUtils.notNull;

public abstract class AbstractTokenSource implements TokenSource {

	protected static final class CachedToken {

		public final String token;

		public final long expiry;

		public CachedToken(String token, long expiry) {
			this.token = notNull(token, "token");
			this.expiry = expiry;
		}

	}

	private long useGracePeriod;

	private volatile CachedToken currentToken;

	private final Object lock = new Object();

	public AbstractTokenSource() {}

	@Override
	public long getUseGracePeriod() {
		return useGracePeriod;
	}

	@Override
	public void setUseGracePeriod(long useGracePeriod) {
		this.useGracePeriod = useGracePeriod <= 0L ? TokenSource.DEFAULT_USE_GRACE_PERIOD : useGracePeriod;
	}

	protected abstract CachedToken getFreshToken() throws IOException, AuthorizationException;

	@Override
	public String getToken() throws IOException, AuthorizationException {
		long now = System.currentTimeMillis();
		if(currentToken == null || currentToken.expiry < now + useGracePeriod) {
			synchronized(lock) {
				if(currentToken == null || currentToken.expiry < now + useGracePeriod) {
					CachedToken freshToken = getFreshToken();
					if(freshToken == null)
						throw new IllegalStateException("Call to getFreshToken() returned null");
					if(freshToken.expiry < now + useGracePeriod)
						throw new IllegalStateException("Freshly fetched token is not valid through the grace period");
					currentToken = freshToken;
				}
			}
		}
		return currentToken.token;
	}

}
