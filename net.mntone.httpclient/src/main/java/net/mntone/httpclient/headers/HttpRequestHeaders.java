package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.Holder;

public final class HttpRequestHeaders extends HttpHeaders
{
	private static final HashMap<String, HttpHeaderParser> _parserStore;
	private static final HashSet<String> _invalidHeaders;

	static
	{
		_parserStore = new HashMap<String, HttpHeaderParser>(HttpGenericHeaders.KNOWN_HEADERS_COUNT + HttpRequestHeaders.KNOWN_HEADERS_COUNT);
		HttpGenericHeaders.putParsers(_parserStore);
		putParsers(_parserStore);

		_invalidHeaders = new HashSet<String>(HttpContentHeaders.KNOWN_HEADERS_COUNT);
		HttpContentHeaders.addKnownHeaders(_invalidHeaders);
	}

	static void putParsers(final Map<String, HttpHeaderParser> parserStore)
	{
		parserStore.put(HttpHeaderNames.HOST, HttpGenericHeaderParser.HostParser);
		parserStore.put(HttpHeaderNames.IF_MATCH, HttpGenericHeaderParser.MultipleValueEntityTagParser);
		parserStore.put(HttpHeaderNames.IF_NONE_MATCH, HttpGenericHeaderParser.MultipleValueEntityTagParser);
		parserStore.put(HttpHeaderNames.TE, TransferEncodingWithQualityHeaderParser.MultipleValuesParser);
	}

	static int KNOWN_HEADERS_COUNT = 19;
	static void addKnownHeaders(final Set<String> headerSet)
	{
		headerSet.add(HttpHeaderNames.ACCEPT);
		headerSet.add(HttpHeaderNames.ACCEPT_CHARSET);
		headerSet.add(HttpHeaderNames.ACCEPT_ENCODING);
		headerSet.add(HttpHeaderNames.ACCEPT_LANGUAGE);
		headerSet.add(HttpHeaderNames.AUTHORIZATION);
		headerSet.add(HttpHeaderNames.EXPECT);
		headerSet.add(HttpHeaderNames.FROM);
		headerSet.add(HttpHeaderNames.HOST);
		headerSet.add(HttpHeaderNames.IF_MATCH);
		headerSet.add(HttpHeaderNames.IF_MODIFIED_SINCE);
		headerSet.add(HttpHeaderNames.IF_NONE_MATCH);
		headerSet.add(HttpHeaderNames.IF_RANGE);
		headerSet.add(HttpHeaderNames.IF_UNMODIFIED_SINCE);
		headerSet.add(HttpHeaderNames.MAX_FORWARDS);
		headerSet.add(HttpHeaderNames.PROXY_AUTHENTICATE);
		headerSet.add(HttpHeaderNames.RANGE);
		headerSet.add(HttpHeaderNames.REFERER);
		headerSet.add(HttpHeaderNames.TE);
		headerSet.add(HttpHeaderNames.USER_AGENT);
	}


	private final HttpGenericHeaders _genericHeaders;

	public HttpRequestHeaders()
	{
		this._genericHeaders = new HttpGenericHeaders(this);
		super.setConfiguration(_parserStore, _invalidHeaders);
	}

	public HttpHeaderValueCollection<String> getConnection()
	{
		return this._genericHeaders.getConnection();
	}

	public Boolean getConnectionClose()
	{
		return this._genericHeaders.getConnectionClose();
	}
	public void setConnectionCore(final Boolean value)
	{
		this._genericHeaders.setConnectionClose(value);
	}

	public String getHost()
	{
		return (String)super.getParsedValue(HttpHeaderNames.HOST);
	}
	public void setHost(final String value)
	{
		if (value == null || value.isEmpty()) throw new IllegalArgumentException();

		final Holder<Integer> index = new Holder<Integer>(0);
		final Holder<String> host = new Holder<String>();
		if (HttpRuleParser.getHostLength(value, index, false, host) != value.length()) throw new IllegalArgumentException();
		super.setOrRemoveParsedValue(HttpHeaderNames.HOST, value);
	}

	public Date getDate()
	{
		return this._genericHeaders.getDate();
	}
	public void setDate(final Date value)
	{
		this._genericHeaders.setDate(value);
	}

	public HttpHeaderValueCollection<EntityTagHeaderValue> getIfMatch()
	{
		if (this._isMatch == null)
		{
			this._isMatch = new HttpHeaderValueCollection<EntityTagHeaderValue>(HttpHeaderNames.IF_MATCH, this, EntityTagHeaderValue.class);
		}
		return this._isMatch;
	}
	private HttpHeaderValueCollection<EntityTagHeaderValue> _isMatch;

	public HttpHeaderValueCollection<EntityTagHeaderValue> getIfNoneMatch()
	{
		if (this._isNoneMatch == null)
		{
			this._isNoneMatch = new HttpHeaderValueCollection<EntityTagHeaderValue>(HttpHeaderNames.IF_NONE_MATCH, this, EntityTagHeaderValue.class);
		}
		return this._isNoneMatch;
	}
	private HttpHeaderValueCollection<EntityTagHeaderValue> _isNoneMatch;

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
}