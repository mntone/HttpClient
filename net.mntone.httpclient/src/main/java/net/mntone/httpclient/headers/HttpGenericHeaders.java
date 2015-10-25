package net.mntone.httpclient.headers;

import java.util.Map;
import java.util.Set;

final class HttpGenericHeaders
{
	static void putParsers(final Map<String, HttpHeaderParser> parserStore)
	{
		parserStore.put(HttpHeaderNames.CONNECTION, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.TRAILER, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.TRANSFER_ENCODING, TransferEncodingHeaderParser.MultipleValuesParser);
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

	private boolean _transferEncodingChunkedSet = false;

	public HttpGenericHeaders(final HttpHeaders parent)
	{
		this._parent = parent;
	}

	HttpHeaderValueCollection<String> getConnectionCore()
	{
		if (this._connectionCore == null)
		{
			this._connectionCore = new HttpHeaderValueCollection<String>(HttpHeaderNames.CONNECTION, this._parent, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._connectionCore;
	}
	private HttpHeaderValueCollection<String> _connectionCore;

	public HttpHeaderValueCollection<String> getTrailer()
	{
		if (this._trailer == null)
		{
			this._trailer = new HttpHeaderValueCollection<String>(HttpHeaderNames.TRAILER, this._parent, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._trailer;
	}
	private HttpHeaderValueCollection<String> _trailer;


	public HttpHeaderValueCollection<TransferEncodingHeaderValue> getTransferEncoding()
	{
		return this.getTransferEncodingCore();
	}

	public Boolean getTransferEncodingChunked()
	{
		if (this.getTransferEncodingCore().isSpecialValueSet()) return true;
		if (this._transferEncodingChunkedSet) return false;
		return null;
	}
	public void setTransferEncodingChunked(final Boolean value)
	{
		if (value)
		{
			this._transferEncodingChunkedSet = true;
			this._transferEncodingCore.setSpecialValue();
		}
		else
		{
			this._transferEncodingChunkedSet = false;
			this._transferEncodingCore.removeSpecialValue();
		}
	}

	HttpHeaderValueCollection<TransferEncodingHeaderValue> getTransferEncodingCore()
	{
		if (this._transferEncodingCore == null)
		{
			this._transferEncodingCore = new HttpHeaderValueCollection<TransferEncodingHeaderValue>(HttpHeaderNames.TRANSFER_ENCODING,
			                                                                                        this._parent,
			                                                                                        HttpHeaderUtils.TRANSFER_ENCODING_CHUNKCED,
			                                                                                        TransferEncodingHeaderValue.class);
		}
		return this._transferEncodingCore;
	}
	private HttpHeaderValueCollection<TransferEncodingHeaderValue> _transferEncodingCore;
}