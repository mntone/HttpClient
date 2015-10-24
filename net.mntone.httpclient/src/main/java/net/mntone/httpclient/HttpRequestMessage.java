package net.mntone.httpclient;

import net.mntone.httpclient.headers.HttpRequestHeaders;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequestMessage implements Closeable
{
	private volatile boolean _closed = false;

	private HttpMethod _method = null;
	private URL _requestUrl = null;
	private HttpRequestHeaders _headers = null;
	private HttpContent _content = null;

	public HttpRequestMessage()
	{
		this.initializeValues(HttpMethod.Get, null);
	}

	public HttpRequestMessage(final HttpMethod method, final URL requestUrl)
	{
		this.initializeValues(method, requestUrl);
	}

	public HttpRequestMessage(final HttpMethod method, final String requestUrlText) throws MalformedURLException
	{
		if (requestUrlText == null || requestUrlText.isEmpty())
		{
			this.initializeValues(method, null);
		}
		else
		{
			this.initializeValues(method, new URL(requestUrlText));
		}
	}

	private void initializeValues(final HttpMethod method, final URL requestUrl)
	{
		if (method == null) throw new IllegalArgumentException();
		if (requestUrl == null || (!HttpUtil.isHttpUrl(requestUrl)))
		{
			throw new IllegalArgumentException();
		}

		this._method = method;
		this._requestUrl = requestUrl;
	}

	@Override
	public final void close() throws IOException
	{
		this.close(true);
	}

	protected void close(final boolean closing) throws IOException
	{
		if (closing && !this._closed)
		{
			this._closed = true;
			if (this._content != null)
			{
				this._content.close();
				this._content = null;
			}
		}
	}

	private void checkClosed() throws IllegalStateException
	{
		if (this._closed)
		{
			throw new IllegalStateException("Already closed.");
		}
	}

	public HttpMethod getMethod()
	{
		return this._method;
	}

	public void setMethod(final HttpMethod value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException();
		}
		this.checkClosed();
		this._method = value;
	}

	public URL getRequestUrl()
	{
		return this._requestUrl;
	}

	public void setRequestUrl(final URL value)
	{
		if (value == null || !HttpUtil.isHttpUrl(value)) throw new IllegalArgumentException();
		this.checkClosed();
		this._requestUrl = value;
	}

	public HttpRequestHeaders getHeaders()
	{
		if (this._headers == null)
		{
			this._headers = new HttpRequestHeaders();
		}
		return this._headers;
	}

	public HttpContent getContent()
	{
		return this._content;
	}

	public void setContent(final HttpContent content)
	{
		this.checkClosed();
		this._content = content;
	}
}