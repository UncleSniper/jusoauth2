package org.unclesniper.oauth2;

import java.io.IOException;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import org.unclesniper.json.JSONParser;
import java.nio.charset.StandardCharsets;
import org.unclesniper.util.http.HTTPVerb;
import org.unclesniper.util.http.HTTPClient;
import java.io.UnsupportedEncodingException;
import org.unclesniper.util.http.HTTPRequest;
import org.unclesniper.util.http.HTTPResponse;
import org.unclesniper.json.MalformedJSONException;
import org.unclesniper.util.http.URLConnectionHTTPClient;

import static org.unclesniper.util.ArgUtils.notNull;

public abstract class AbstractHTTPTokenSource extends AbstractTokenSource {

	private static final HTTPClient DEFAULT_HTTP_CLIENT = new URLConnectionHTTPClient();

	private static final int MAX_RESPONSE_LENGTH = 512 * 1024;

	private HTTPClient httpClient;

	public AbstractHTTPTokenSource() {}

	public HTTPClient getHTTPClient() {
		return httpClient;
	}

	public void setHTTPClient(HTTPClient httpClient) {
		this.httpClient = httpClient;
	}

	protected final HTTPClient getEffectiveHTTPClient() {
		return httpClient == null ? AbstractHTTPTokenSource.DEFAULT_HTTP_CLIENT : httpClient;
	}

	protected abstract String getEndpointURL() throws IOException, AuthorizationException;

	protected abstract void setRequestParameters(ParameterSink sink) throws IOException, AuthorizationException;

	@Override
	protected CachedToken getFreshToken() throws IOException, AuthorizationException {
		HTTPRequest req = getEffectiveHTTPClient().request(HTTPVerb.POST, getEndpointURL());
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		StringBuilder body = new StringBuilder();
		setRequestParameters((name, value) -> {
			if(notNull(name, "name").length() == 0)
				throw new IllegalArgumentException("Parameter name must not be empty");
			notNull(value, "value");
			if(body.length() > 0)
				body.append('&');
			try {
				body.append(URLEncoder.encode(name, "UTF-8"));
				body.append('=');
				body.append(URLEncoder.encode(value, "UTF-8"));
			}
			catch(UnsupportedEncodingException uee) {
				throw new Error("JVM does not support UTF-8!?", uee);
			}
		});
		byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
		req.setRequestBody(new ByteArrayInputStream(bytes));
		String respBody;
		long now = System.currentTimeMillis();
		try(HTTPResponse resp = req.request()) {
			String charset = resp.getContentCharset();
			InputStreamReader reader = new InputStreamReader(resp.getResponseBody(),
					charset == null ? "UTF-8" : charset);
			char[] buffer = new char[512];
			StringBuilder respBuilder = new StringBuilder();
			int haveLength = 0;
			for(;;) {
				int count = reader.read(buffer);
				if(count <= 0)
					break;
				if(haveLength + count > AbstractHTTPTokenSource.MAX_RESPONSE_LENGTH)
					throw new ResponsePayloadTooLongException(respBuilder.toString());
				respBuilder.append(buffer, 0, count);
				haveLength += count;
			}
			respBody = respBuilder.toString();
			int status = resp.getResponseCode();
			if(status != 200)
				throw new BadResponseStatusCodeException(status, respBody);
		}
		TokenResponseJSONSink jsonSink = new TokenResponseJSONSink();
		JSONParser jsonParser = new JSONParser(jsonSink);
		try {
			jsonParser.pushSerial(respBody);
			jsonParser.endDocument();
		}
		catch(IOException | MalformedJSONException e) {
			throw new MalformedResponsePayloadException(respBody, e);
		}
		String token = jsonSink.getToken();
		if(token == null)
			throw new MissingResponsePropertyException("access_token", respBody);
		long expiresIn = jsonSink.getExpiresIn();
		if(expiresIn <= 0L)
			throw new MissingResponsePropertyException("expires_in", respBody);
		return new CachedToken(token, now + expiresIn * 1000L);
	}

}
