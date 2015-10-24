package net.mntone.httpclient;

import jersey.repackaged.jsr166e.CompletableFuture;

public abstract class DelegatingHandler extends HttpMessageHandler
{
	private volatile boolean _closed = false;
	private volatile boolean _operationStarted = false;

	private HttpMessageHandler _innerHandler = null;

	protected DelegatingHandler()
	{
	}

	protected DelegatingHandler(final HttpMessageHandler innerHandler)
	{
		this._innerHandler = innerHandler;
	}

	@Override
	protected void close(final boolean closing)
	{
		if (closing && !this._closed)
		{
			this._closed = true;
		}
		super.close(closing);
	}

	private void checkClosed() throws IllegalStateException
	{
		if (this._closed)
		{
			throw new IllegalStateException("Already closed.");
		}
	}

	@Override
	CompletableFuture<HttpResponseMessage> sendAsync(final HttpRequestMessage request)
	{
		if (request == null)
		{
			throw new IllegalArgumentException();
		}
		this.setOperationStarted();
		return this._innerHandler.sendAsync(request);
	}

	private void checkClosedOrStarted() throws IllegalAccessException
	{
		this.checkClosed();
		if (this._operationStarted)
		{
			throw new IllegalAccessException();
		}
	}

	private void setOperationStarted()
	{
		this.checkClosed();
		if (this._innerHandler == null)
		{
			throw new IllegalStateException();
		}
		if (!this._operationStarted)
		{
			this._operationStarted = true;
		}
	}

	public final HttpMessageHandler getInnerHandler()
	{
		return this._innerHandler;
	}

	public final void setInnerHandler(final HttpMessageHandler value) throws IllegalAccessException
	{
		if (value == null)
		{
			throw new IllegalArgumentException();
		}
		this.checkClosedOrStarted();
		this._innerHandler = value;
	}
}