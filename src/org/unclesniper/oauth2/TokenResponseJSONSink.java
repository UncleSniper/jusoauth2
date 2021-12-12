package org.unclesniper.oauth2;

import java.io.IOException;
import org.unclesniper.json.JSONSink;

public class TokenResponseJSONSink implements JSONSink {

	public static class TokenResponseJSONException extends IOException {

		public TokenResponseJSONException(String message) {
			super(message);
		}

		public TokenResponseJSONException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	public static class UnexpectedTokenResponseJSONStructureException extends TokenResponseJSONException {

		private final String found;

		private final String expected;

		private final String near;

		public UnexpectedTokenResponseJSONStructureException(String found, String expected, String near) {
			super("Found " + found + " where " + expected + " was expected near " + near);
			this.found = found;
			this.expected = expected;
			this.near = near;
		}

		public String getFound() {
			return found;
		}

		public String getExpected() {
			return expected;
		}

		public String getNear() {
			return near;
		}

	}

	public static class IllegalExpiresInValueTokenResponseJSONException extends TokenResponseJSONException {

		private final long expiresIn;

		public IllegalExpiresInValueTokenResponseJSONException(long expiresIn) {
			super("Illegal value in 'expires_in' property: " + expiresIn);
			this.expiresIn = expiresIn;
		}

		public long getExpiresIn() {
			return expiresIn;
		}

	}

	private enum State {

		NONE("'{'"),
		BEFORE_KEY("string"),
		BEFORE_ACCESS_TOKEN("string"),
		BEFORE_EXPIRES_IN("number"),
		BEFORE_IGNORED_VALUE("primitive"),
		AFTER_OBJECT("end-of-document");

		private State(String expected) {
			this.expected = expected;
		}

		private final String expected;

		public String getExpected() {
			return expected;
		}

	}

	private State state = State.NONE;

	private String token;

	private long expiresIn;

	public TokenResponseJSONSink() {}

	public String getToken() {
		return token;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	private void unexpected(String what, String rend) throws UnexpectedTokenResponseJSONStructureException {
		throw new UnexpectedTokenResponseJSONStructureException(what, state.getExpected(), rend);
	}

	@Override
	public void foundBoolean(boolean value) throws TokenResponseJSONException {
		if(state != State.BEFORE_IGNORED_VALUE)
			unexpected("boolean", value ? "'true'" : "'false'");
		state = State.BEFORE_KEY;
	}

	@Override
	public void foundNull() throws TokenResponseJSONException {
		if(state != State.BEFORE_IGNORED_VALUE)
			unexpected("null value", "'null'");
		state = State.BEFORE_KEY;
	}

	@Override
	public void foundString(String value) throws TokenResponseJSONException {
		switch(state) {
			case BEFORE_KEY:
				switch(value) {
					case "access_token":
						state = State.BEFORE_ACCESS_TOKEN;
						break;
					case "expires_in":
						state = State.BEFORE_EXPIRES_IN;
						break;
					default:
						state = State.BEFORE_IGNORED_VALUE;
						break;
				}
				break;
			case BEFORE_ACCESS_TOKEN:
				token = value;
				state = State.BEFORE_KEY;
				break;
			case BEFORE_IGNORED_VALUE:
				state = State.BEFORE_KEY;
				break;
			case BEFORE_EXPIRES_IN:
				{
					long longValue;
					try {
						longValue = Long.parseLong(value);
					}
					catch(NumberFormatException nfe) {
						unexpected("string", "'\"" + value + "\"'");
						return;
					}
					foundInteger(longValue);
				}
				break;
			default:
				unexpected("string", "'\"" + value + "\"'");
		}
	}

	private void foundNumber(long value, String what, String rend) throws TokenResponseJSONException {
		switch(state) {
			case BEFORE_EXPIRES_IN:
				if(value <= 0L)
					throw new IllegalExpiresInValueTokenResponseJSONException(value);
				expiresIn = value;
				state = State.BEFORE_KEY;
				break;
			case BEFORE_IGNORED_VALUE:
				state = State.BEFORE_KEY;
				break;
			default:
				unexpected(what, rend);
		}
	}

	@Override
	public void foundInteger(long value) throws TokenResponseJSONException {
		foundNumber(value, "integer", '\'' + String.valueOf(value) + '\'');
	}

	@Override
	public void foundFraction(double value) throws TokenResponseJSONException {
		foundNumber((long)value, "fraction", '\'' + String.valueOf(value) + '\'');
	}

	@Override
	public void beginObject() throws TokenResponseJSONException {
		if(state != State.NONE)
			unexpected("start-of-object", "'{'");
		state = State.BEFORE_KEY;
	}

	@Override
	public void endObject() throws TokenResponseJSONException {
		if(state != State.BEFORE_KEY)
			unexpected("end-of-object", "'}'");
		state = State.AFTER_OBJECT;
	}

	@Override
	public void beginArray() throws TokenResponseJSONException {
		unexpected("start-of-array", "'['");
	}

	@Override
	public void endArray() throws TokenResponseJSONException {
		unexpected("end-of-array", "']'");
	}

}
