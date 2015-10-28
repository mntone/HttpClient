package net.mntone.httpclient.headers;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class HttpResponseHeaders extends HttpHeaders
{
	private static final HashMap<String, HttpHeaderParser> _parserStore;
	private static final HashSet<String> _invalidHeaders;

	static
	{
		_parserStore = new HashMap<String, HttpHeaderParser>(HttpGenericHeaders.KNOWN_HEADERS_COUNT + HttpResponseHeaders.KNOWN_HEADERS_COUNT);
		HttpGenericHeaders.putParsers(_parserStore);
		putParsers(_parserStore);

		_invalidHeaders = new HashSet<String>(HttpContentHeaders.KNOWN_HEADERS_COUNT);
		HttpContentHeaders.addKnownHeaders(_invalidHeaders);
	}

	static void putParsers(final Map<String, HttpHeaderParser> parserStore)
	{
		parserStore.put(HttpHeaderNames.ACCEPT_RANGES, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.ENTITY_TAG, HttpGenericHeaderParser.SingleValueEntityTagParser);
		parserStore.put(HttpHeaderNames.VARY, HttpGenericHeaderParser.TokenListParser);
	}

	static int KNOWN_HEADERS_COUNT = 9;
	static void addKnownHeaders(final Set<String> headerSet)
	{
		headerSet.add(HttpHeaderNames.ACCEPT_RANGES);
		headerSet.add(HttpHeaderNames.AGE);
		headerSet.add(HttpHeaderNames.ENTITY_TAG);
		headerSet.add(HttpHeaderNames.LOCATION);
		headerSet.add(HttpHeaderNames.PROXY_AUTHENTICATE);
		headerSet.add(HttpHeaderNames.RETRY_AFTER);
		headerSet.add(HttpHeaderNames.SERVER);
		headerSet.add(HttpHeaderNames.VARY);
		headerSet.add(HttpHeaderNames.WWW_AUTHENTICATE);
	}


	private final HttpGenericHeaders _genericHeaders;

	public HttpResponseHeaders()
	{
		this._genericHeaders = new HttpGenericHeaders(this);
		super.setConfiguration(_parserStore, _invalidHeaders);
	}

	@Override
	void addHeaders(final HttpHeaders sourceHeaders)
	{
		super.addHeaders(sourceHeaders);

		final HttpResponseHeaders httpResponseHeaders = (HttpResponseHeaders)sourceHeaders;
	}

	public HttpHeaderValueCollection<String> getAcceptRanges()
	{
		if (this._acceptRanges == null)
		{
			this._acceptRanges = new HttpHeaderValueCollection<String>(HttpHeaderNames.ACCEPT_RANGES, this, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._acceptRanges;
	}
	private HttpHeaderValueCollection<String> _acceptRanges;

	public HttpHeaderValueCollection<String> getConnection()
	{
		return this._genericHeaders.getConnectionCore();
	}

	public Date getDate()
	{
		return this._genericHeaders.getDate();
	}
	public void setDate(final Date value)
	{
		this._genericHeaders.setDate(value);
	}

	public EntityTagHeaderValue getETag()
	{
		return (EntityTagHeaderValue)super.getParsedValue(HttpHeaderNames.ENTITY_TAG);
	}
	public void setETag(final EntityTagHeaderValue value)
	{
		super.setOrRemoveParsedValue(HttpHeaderNames.ENTITY_TAG, value);
	}

	public HttpHeaderValueCollection<NameValueHeaderValue> getPragma()
	{
		return this._genericHeaders.getPragma();
	}

	public HttpHeaderValueCollection<String> getTrailer()
	{
		return this._genericHeaders.getTrailer();
	}

	public HttpHeaderValueCollection<TransferEncodingHeaderValue> getTransferEncoding()
	{
		return this._genericHeaders.getTransferEncoding();
	}

	public Boolean getTransferEncodingChunked()
	{
		return this._genericHeaders.getTransferEncodingChunked();
	}
	public void setTransferEncodingChunked(final Boolean value)
	{
		this._genericHeaders.setTransferEncodingChunked(value);
	}

	public HttpHeaderValueCollection<String> getVary()
	{
		if (this._vary == null)
		{
			this._vary = new HttpHeaderValueCollection<String>(HttpHeaderNames.VARY, this, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._vary;
	}
	private HttpHeaderValueCollection<String> _vary;
}