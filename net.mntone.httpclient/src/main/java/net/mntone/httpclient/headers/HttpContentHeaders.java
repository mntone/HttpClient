package net.mntone.httpclient.headers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class HttpContentHeaders extends HttpHeaders
{
	private static final HashMap<String, HttpHeaderParser> _parserStore;
	private static final HashSet<String> _invalidHeaders;

	static
	{
		_parserStore = new HashMap<String, HttpHeaderParser>(HttpContentHeaders.KNOWN_HEADERS_COUNT);
		HttpContentHeaders.putParsers(_parserStore);

		_invalidHeaders = new HashSet<String>(0);
	}

	static void putParsers(final Map<String, HttpHeaderParser> parserStore)
	{
		parserStore.put(HttpHeaderNames.ALLOW, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.CONTENT_ENCODING, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.CONTENT_LANGUAGE, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.CONTENT_LENGTH, Int64NumberHeaderParser.Instance);
		parserStore.put(HttpHeaderNames.CONTENT_TYPE, MediaTypeHeaderParser.SingleValueParser);
	}

	static int KNOWN_HEADERS_COUNT = 11;
	static void addKnownHeaders(final Set<String> headerSet)
	{
		headerSet.add(HttpHeaderNames.ALLOW);
		headerSet.add(HttpHeaderNames.CONTENT_DISPOSITION);
		headerSet.add(HttpHeaderNames.CONTENT_ENCODING);
		headerSet.add(HttpHeaderNames.CONTENT_LANGUAGE);
		headerSet.add(HttpHeaderNames.CONTENT_LENGTH);
		headerSet.add(HttpHeaderNames.CONTENT_LOCATION);
		headerSet.add(HttpHeaderNames.CONTENT_MD5);
		headerSet.add(HttpHeaderNames.CONTENT_RANGE);
		headerSet.add(HttpHeaderNames.CONTENT_TYPE);
		headerSet.add(HttpHeaderNames.EXPIRES);
		headerSet.add(HttpHeaderNames.LAST_MODIFIED);
	}

	public interface LengthCalculator
	{
		Long apply();
	}

	private final LengthCalculator _calculator;

	private boolean contentLengthSet = false;

	public HttpContentHeaders(final LengthCalculator calculator)
	{
		this._calculator = calculator;
		super.setConfiguration(_parserStore, _invalidHeaders);
	}

	public HttpHeaderValueCollection<String> getAllow()
	{
		if (this._allow == null)
		{
			this._allow = new HttpHeaderValueCollection<String>(HttpHeaderNames.ALLOW, this, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._allow;
	}
	private HttpHeaderValueCollection<String> _allow;

	public HttpHeaderValueCollection<String> getContentEncoding()
	{
		if (this._contentEncoding == null)
		{
			this._contentEncoding = new HttpHeaderValueCollection<String>(HttpHeaderNames.CONTENT_ENCODING, this, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._contentEncoding;
	}
	private HttpHeaderValueCollection<String> _contentEncoding;

	public HttpHeaderValueCollection<String> getContentLanguage()
	{
		if (this._contentLanguage == null)
		{
			this._contentLanguage = new HttpHeaderValueCollection<String>(HttpHeaderNames.CONTENT_LANGUAGE, this, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._contentLanguage;
	}
	private HttpHeaderValueCollection<String> _contentLanguage;

	public Long getContentLength()
	{
		final Object parsedValue = super.getParsedValue(HttpHeaderNames.CONTENT_LENGTH);
		if (!this.contentLengthSet && parsedValue == null)
		{
			final Long result = this._calculator.apply();
			if (result != null)
			{
				super.setParsedValue(HttpHeaderNames.CONTENT_LENGTH, result);
			}
			return result;
		}
		return (Long)parsedValue;
	}
	public void setContentLength(final Long value)
	{
		super.setOrRemoveParsedValue(HttpHeaderNames.CONTENT_LENGTH, value);
		contentLengthSet = true;
	}

	public MediaTypeHeaderValue getContentType()
	{
		return (MediaTypeHeaderValue)super.getParsedValue(HttpHeaderNames.CONTENT_TYPE);
	}
	public void setContentType(final MediaTypeHeaderValue value)
	{
		super.setOrRemoveParsedValue(HttpHeaderNames.CONTENT_TYPE, value);
	}
}