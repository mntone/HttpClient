package net.mntone.httpclient.headers;

import java.util.Date;
import java.util.Map;
import java.util.Set;

final class HttpGenericHeaders
{
	static void putParsers(final Map<String, HttpHeaderParser> parserStore)
	{
		parserStore.put(HttpHeaderNames.CONNECTION, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.DATE, DateHeaderParser.Instance);
		parserStore.put(HttpHeaderNames.PRAGMA, HttpGenericHeaderParser.MultipleValueNameValueParser);
		parserStore.put(HttpHeaderNames.TRAILER, HttpGenericHeaderParser.TokenListParser);
		parserStore.put(HttpHeaderNames.TRANSFER_ENCODING, TransferEncodingHeaderParser.MultipleValuesParser);
		parserStore.put(HttpHeaderNames.UPGRADE, HttpGenericHeaderParser.MultipleValueProductParser);
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

	final void addSpecialsFrom(final HttpGenericHeaders sourceHeaders)
	{
		if (this.getConnectionClose() == null)
		{
			this.setConnectionClose(sourceHeaders.getConnectionClose());
		}
		if (this.getTransferEncodingChunked() == null)
		{
			this.setTransferEncodingChunked(sourceHeaders.getTransferEncodingChunked());
		}
	}

	private final HttpHeaders _parent;

	private boolean _connectionCloseSet = false;
	private boolean _transferEncodingChunkedSet = false;

	public HttpGenericHeaders(final HttpHeaders parent)
	{
		this._parent = parent;
	}

	public HttpHeaderValueCollection<String> getConnection()
	{
		return this.getConnectionCore();
	}

	public Boolean getConnectionClose()
	{
		if (this.getConnectionCore().isSpecialValueSet()) return true;
		if (this._connectionCloseSet) return false;
		return null;
	}
	public void setConnectionClose(final Boolean value)
	{
		if (value)
		{
			this._connectionCloseSet = true;
			this.getConnectionCore().setSpecialValue();
		}
		else
		{
			this._connectionCloseSet = false;
			this.getConnectionCore().removeSpecialValue();
		}
	}

	private HttpHeaderValueCollection<String> getConnectionCore()
	{
		if (this._connectionCore == null)
		{
			this._connectionCore = new HttpHeaderValueCollection<String>(HttpHeaderNames.CONNECTION, this._parent, HttpHeaderUtils.getTokenValidator(), String.class);
		}
		return this._connectionCore;
	}
	private HttpHeaderValueCollection<String> _connectionCore;


	public Date getDate()
	{
		return (Date)this._parent.getParsedValue(HttpHeaderNames.DATE);
	}
	public void setDate(final Date value)
	{
		this._parent.setOrRemoveParsedValue(HttpHeaderNames.DATE, value);
	}

	public HttpHeaderValueCollection<NameValueHeaderValue> getPragma()
	{
		if (this._pragma == null)
		{
			this._pragma = new HttpHeaderValueCollection<NameValueHeaderValue>(HttpHeaderNames.PRAGMA, this._parent, NameValueHeaderValue.class);
		}
		return this._pragma;
	}
	private HttpHeaderValueCollection<NameValueHeaderValue> _pragma;

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

	private HttpHeaderValueCollection<TransferEncodingHeaderValue> getTransferEncodingCore()
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

	public HttpHeaderValueCollection<ProductInfoHeaderParser> getUpgrade()
	{
		if (this._upgrade == null)
		{
			this._upgrade = new HttpHeaderValueCollection<ProductInfoHeaderParser>(HttpHeaderNames.UPGRADE, this._parent, ProductInfoHeaderParser.class);
		}
		return this._upgrade;
	}
	private HttpHeaderValueCollection<ProductInfoHeaderParser> _upgrade;
}