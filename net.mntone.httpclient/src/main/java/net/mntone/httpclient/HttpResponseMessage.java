package net.mntone.httpclient;

import net.mntone.httpclient.headers.HttpResponseHeaders;

import java.io.Closeable;
import java.io.IOException;

public final class HttpResponseMessage implements Closeable
{
	private volatile boolean _closed = false;

	private HttpRequestMessage _requestMessage = null;

	private HttpStatusCode _statusCode;
	private String _reasonPhrase = null;
	private HttpResponseHeaders _headers = null;
	private HttpContent _content = null;

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

	public HttpRequestMessage getRequestMessage()
	{
		return this._requestMessage;
	}
	public void setRequestMessage(final HttpRequestMessage value)
	{
		this.checkClosed();
		this._requestMessage = value;
	}

	public HttpStatusCode getStatusCode()
	{
		return this._statusCode;
	}
	public void setStatusCode(final HttpStatusCode value)
	{
		this.checkClosed();
		this._statusCode = value;
	}

	public String getReasonPhrase()
	{
		if (this._reasonPhrase == null)
		{
			return HttpStatusDescription.getStatusDescription(this.getStatusCode());
		}
		return this._reasonPhrase;
	}
	public void setReasonPhrase(final String value)
	{
		if (value != null && containsNewLineCharacter(value))
		{
			throw new IllegalArgumentException();
		}
		this.checkClosed();
		this._reasonPhrase = value;
	}

	public HttpResponseHeaders getHeaders()
	{
		if (this._headers == null)
		{
			this._headers = new HttpResponseHeaders();
		}
		return this._headers;
	}

	public HttpContent getContent()
	{
		return this._content;
	}
	public void setContent(final HttpContent value)
	{
		this.checkClosed();
		this._content = value;
	}

	private static boolean containsNewLineCharacter(final String value)
	{
		final int length = value.length();
		for (int i = 0; i < length; ++i)
		{
			final char c = value.charAt(i);
			if (c == '\r' || c == '\n') return true;
		}
		return false;
	}
}