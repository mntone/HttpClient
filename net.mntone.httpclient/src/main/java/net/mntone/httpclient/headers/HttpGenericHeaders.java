package net.mntone.httpclient.headers;

import java.util.Map;
import java.util.Set;

final class HttpGenericHeaders
{
	static void putParsers(final Map<String, HttpHeaderParser> parserStore)
	{
		parserStore.put(HttpHeaderNames.CONNECTION, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.TRAILER, HttpGenericHeaderParser.TokenListParser);
	}

	static int KNOWN_HEADERS_COUNT = 9;
	static void addKnownHeaders(final Set<String> headerSet)
	{
		headerSet.add(HttpHeaderNames.CACHE_CONTROL);
		headerSet.add(HttpHeaderNames.CONNECTION);
		headerSet.add(HttpHeaderNames.DATE);
		headerSet.add(HttpHeaderNames.PRAGMA);
		headerSet.add(HttpHeaderNames.TRAILER);
		headerSet.add(HttpHeaderNames.TRANSFER_ENCODING);
		headerSet.add(HttpHeaderNames.UPGRADE);
		headerSet.add(HttpHeaderNames.VIA);
		headerSet.add(HttpHeaderNames.WARNING);
	}

	private final HttpHeaders _parent;

	public HttpGenericHeaders(final HttpHeaders parent)
	{
		this._parent = parent;
	}

	public HttpHeaderValueCollection<String> getConnectionCore()
	{
		if (this._connectionCore == null)
		{
			this._connectionCore = new HttpHeaderValueCollection<String>(HttpHeaderNames.CONNECTION, this._parent, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._connectionCore;
	}
	private HttpHeaderValueCollection<String> _connectionCore;

	public HttpHeaderValueCollection<String> getTrailerCore()
	{
		if (this._trailerCore == null)
		{
			this._trailerCore = new HttpHeaderValueCollection<String>(HttpHeaderNames.TRAILER, this._parent, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._trailerCore;
	}
	private HttpHeaderValueCollection<String> _trailerCore;
}