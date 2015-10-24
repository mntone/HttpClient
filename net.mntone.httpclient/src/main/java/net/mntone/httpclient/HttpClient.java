package net.mntone.httpclient;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import jersey.repackaged.jsr166e.CompletableFuture;

public class HttpClient implements Closeable
{
	private volatile boolean _closed = false;

	private boolean _closeHandler = true;
	private HttpMessageHandler _handler = null;

	public HttpClient()
	{
		this(new HttpClientHandler(), true);
	}

	public HttpClient(final HttpMessageHandler handler)
	{
		this(handler, true);
	}

	public HttpClient(final HttpMessageHandler handler, final boolean closeHandler)
	{
		if (handler == null)
		{
			throw new IllegalArgumentException();
		}
		this._handler = handler;
		this._closeHandler = closeHandler;
	}

	//region Close Methods

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
			if (this._closeHandler)
			{
				this._handler.close();
				this._handler = null;
			}
		}
	}

	private void checkClosed()
	{
		if (this._closed)
		{
			throw new IllegalStateException("Already closed.");
		}
	}

	//endregion

	public CompletableFuture<HttpResponseMessage> sendAsync(final HttpRequestMessage request)
	{
		if (request == null)
		{
			throw new IllegalArgumentException();
		}
		this.checkClosed();
		return this._handler.sendAsync(request).thenApplyAsync(new CompletableFuture.Fun<HttpResponseMessage, HttpResponseMessage>()
		{
			@Override
			public HttpResponseMessage apply(final HttpResponseMessage response)
			{
				HttpClient.this.closeRequestContent(request);
				return response;
			}
		});
	}

	private void closeRequestContent(final HttpRequestMessage request)
	{
		final HttpContent content = request.getContent();
		if (content != null)
		{
			try
			{
				content.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			request.setContent(null);
		}
	}

	public CompletableFuture<HttpResponseMessage> getAsync(final String requestUrlText)
	{
		return this.getAsync(createUrl(requestUrlText));
	}

	public CompletableFuture<HttpResponseMessage> getAsync(final URL requestUrl)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, requestUrl);
		return this.sendAsync(request);
	}

	public CompletableFuture<InputStream> getInputStreamAsync(final String requestUrlText)
	{
		return this.getInputStreamAsync(createUrl(requestUrlText));
	}

	public CompletableFuture<InputStream> getInputStreamAsync(final URL requestUrl)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, requestUrl);
		return this.sendAsync(request).thenApply(new CompletableFuture.Fun<HttpResponseMessage, InputStream>()
		{
			@Override
			public InputStream apply(final HttpResponseMessage response)
			{
				final HttpContent content = response.getContent();
				if (content == null) return null;
				try
				{
					final InputStream contentInputStream = content.readAsInputStreamAsync().get();
					response.close();
					return contentInputStream;
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (final ExecutionException e)
				{
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public CompletableFuture<byte[]> getByteArrayAsync(final String requestUrlText)
	{
		return this.getByteArrayAsync(createUrl(requestUrlText));
	}

	public CompletableFuture<byte[]> getByteArrayAsync(final URL requestUrl)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, requestUrl);
		return this.sendAsync(request).thenApply(new CompletableFuture.Fun<HttpResponseMessage, byte[]>()
		{
			@Override
			public byte[] apply(final HttpResponseMessage response)
			{
				final HttpContent content = response.getContent();
				if (content == null) return null;
				try
				{
					final byte[] contentByteArray = content.readAsByteArrayAsync().get();
					response.close();
					return contentByteArray;
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (final ExecutionException e)
				{
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public CompletableFuture<String> getStringAsync(final String requestUrlText)
	{
		return this.getStringAsync(createUrl(requestUrlText));
	}

	public CompletableFuture<String> getStringAsync(final URL requestUrl)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, requestUrl);
		return this.sendAsync(request).thenApply(new CompletableFuture.Fun<HttpResponseMessage, String>()
		{
			@Override
			public String apply(final HttpResponseMessage response)
			{
				final HttpContent content = response.getContent();
				if (content == null) return "";
				try
				{
					final String contentText = content.readAsStringAsync().get();
					response.close();
					return contentText;
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (final ExecutionException e)
				{
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
				return "";
			}
		});
	}

	public CompletableFuture<HttpResponseMessage> headAsync(final String requestUrlText)
	{
		return this.headAsync(createUrl(requestUrlText));
	}

	public CompletableFuture<HttpResponseMessage> headAsync(final URL requestUrl)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Head, requestUrl);
		return this.sendAsync(request);
	}

	public CompletableFuture<HttpResponseMessage> postAsync(final String requestUrlText, final HttpContent content)
	{
		return this.postAsync(createUrl(requestUrlText), content);
	}

	public CompletableFuture<HttpResponseMessage> postAsync(final URL requestUrl, final HttpContent content)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Post, requestUrl);
		request.setContent(content);
		return this.sendAsync(request);
	}

	public CompletableFuture<HttpResponseMessage> putAsync(final String requestUrlText, final HttpContent content)
	{
		return this.putAsync(createUrl(requestUrlText), content);
	}

	public CompletableFuture<HttpResponseMessage> putAsync(final URL requestUrl, final HttpContent content)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Put, requestUrl);
		request.setContent(content);
		return this.sendAsync(request);
	}

	public CompletableFuture<HttpResponseMessage> deleteAsync(final String requestUrlText)
	{
		return this.deleteAsync(createUrl(requestUrlText));
	}

	public CompletableFuture<HttpResponseMessage> deleteAsync(final URL requestUrl)
	{
		final HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Delete, requestUrl);
		return this.sendAsync(request);
	}

	private static URL createUrl(final String requestUrlText)
	{
		if (requestUrlText == null || requestUrlText.isEmpty()) throw new IllegalArgumentException();

		final URL url;
		try
		{
			url = new URL(requestUrlText);
		}
		catch (final MalformedURLException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return url;
	}
}