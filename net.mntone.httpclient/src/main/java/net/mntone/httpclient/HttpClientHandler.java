package net.mntone.httpclient;

import net.mntone.httpclient.headers.HttpHeaderNames;
import net.mntone.httpclient.headers.HttpRequestHeaders;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import jersey.repackaged.jsr166e.CompletableFuture;
import jersey.repackaged.jsr166e.CompletionException;

public class HttpClientHandler extends HttpMessageHandler
{
	private static class RequestState
	{
		HttpURLConnection httpUrlConnection;
		HttpRequestMessage requestMessage;
	}

	private static final String ACCEPT_ENCODING = "Accept-Encoding";
	private static final String CONTENT_LENGTH = "Content-Length";

	private volatile boolean _closed = false;
	private volatile boolean _operationStarted = false;

	private boolean _allowAutoRedirect = false;
	private EnumSet<DecompressionMethods> _automaticDecompression = EnumSet.of(DecompressionMethods.GZip, DecompressionMethods.Deflate);

	private boolean _useProxy = true;
	private Proxy _proxy = null;

	private boolean _useCookie = true;
	private CookieManager _cookieContainer = new CookieManager();

	public HttpClientHandler()
	{
	}

	//region Close Methods

	@Override
	protected void close(final boolean closing)
	{
		if (closing && !this._closed)
		{
			this._closed = true;
		}
		super.close(closing);
	}

	private void checkClosed()
	{
		if (this._closed)
		{
			throw new IllegalStateException("Already closed.");
		}
	}

	//endregion

	@Override
	CompletableFuture<HttpResponseMessage> sendAsync(final HttpRequestMessage request)
	{
		if (request == null) throw new IllegalArgumentException();
		this.setOperationStarted();

		final RequestState requestState = new RequestState();
		requestState.requestMessage = request;
		return CompletableFuture.supplyAsync(new CompletableFuture.Generator<HttpResponseMessage>()
		{
			@Override
			public HttpResponseMessage get()
			{
				HttpURLConnection httpUrlConnection = null;
				try
				{
					httpUrlConnection = HttpClientHandler.this.createAndPrepareHttpURLConnection(request);
					requestState.httpUrlConnection = httpUrlConnection;
					return HttpClientHandler.this.startRequest(requestState);
				}
				catch (final Exception e)
				{
					if (httpUrlConnection != null)
					{
						httpUrlConnection.disconnect();
					}
					e.printStackTrace();
					throw new CompletionException(e.getMessage(), e);
				}
			}
		});
	}

	private void setOperationStarted()
	{
		this.checkClosed();
		if (!this._operationStarted)
		{
			this._operationStarted = true;
		}
	}

	private HttpURLConnection createAndPrepareHttpURLConnection(final HttpRequestMessage request) throws IOException
	{
		HttpURLConnection connection = null;
		if (this._useProxy && this._proxy != null)
		{
			connection = (HttpURLConnection)request.getRequestUrl().openConnection(this._proxy);
		}
		else
		{
			connection = (HttpURLConnection)request.getRequestUrl().openConnection();
		}
		connection.setRequestMethod(request.getMethod().toString());
		this.setDefaultOptions(connection);
		setRequestHeaders(connection, request);
		setContentHeaders(connection, request);
		initializeHttpURLConnection(connection, request);
		return connection;
	}

	private void setDefaultOptions(final HttpURLConnection connection)
	{
		connection.setConnectTimeout(0);
		connection.setInstanceFollowRedirects(this._allowAutoRedirect);

		final ArrayList<String> acceptEncodings = new ArrayList<String>(2);
		if (this._automaticDecompression.contains(DecompressionMethods.GZip)) acceptEncodings.add("gzip");
		if (this._automaticDecompression.contains(DecompressionMethods.Deflate)) acceptEncodings.add("deflate");
		connection.setRequestProperty(ACCEPT_ENCODING, String.join(", ", acceptEncodings));

		if (this._useCookie) CookieHandler.setDefault(this._cookieContainer);
		else CookieHandler.setDefault(null);
	}

	private HttpResponseMessage startRequest(final RequestState state) throws IOException, ExecutionException, InterruptedException
	{
		if (state.requestMessage.getContent() != null)
		{
			return this.startUploadingContent(state);
		}

		state.httpUrlConnection.setRequestProperty(CONTENT_LENGTH, "0");
		return this.startGettingResponse(state);
	}

	private HttpResponseMessage startUploadingContent(final RequestState state) throws IOException, ExecutionException, InterruptedException
	{
		state.httpUrlConnection.setDoOutput(true);

		final Boolean chunked = state.requestMessage.getHeaders().getTransferEncodingChunked();
		if (chunked != null && chunked)
		{
			state.httpUrlConnection.setChunkedStreamingMode(0);
			final FilterOutputStream outputStream = new FilterOutputStream(state.httpUrlConnection.getOutputStream());
			outputStream.write(state.requestMessage.getContent().readAsByteArrayAsync().get());
		}
		else
		{
			final byte[] content = state.requestMessage.getContent().readAsByteArrayAsync().get();
			state.httpUrlConnection.setRequestProperty(CONTENT_LENGTH, Integer.toString(content.length));

			final FilterOutputStream outputStream = new FilterOutputStream(state.httpUrlConnection.getOutputStream());
			outputStream.write(content);
		}
		return this.createResponseMessage(state.httpUrlConnection, state.requestMessage);
	}

	private HttpResponseMessage startGettingResponse(final RequestState state) throws IOException
	{
		state.httpUrlConnection.connect();
		return this.createResponseMessage(state.httpUrlConnection, state.requestMessage);
	}

	private static void setRequestHeaders(final HttpURLConnection connection, final HttpRequestMessage request)
	{
		final HttpRequestHeaders requestHeaders = request.getHeaders();
		if (requestHeaders.containsKey(HttpHeaderNames.HOST))
		{
			final String host = requestHeaders.getHost();
			if (host != null)
			{
				connection.setRequestProperty(HttpHeaderNames.HOST, host);
			}
		}

		// TODO: other header

		final Iterator<Map.Entry<String, String>> itr = requestHeaders.getHeaderStrings();
		while (itr.hasNext())
		{
			final Map.Entry<String, String> item = itr.next();
			final String name = item.getKey();
			if (!HttpHeaderNames.HOST.equals(name) && !HttpHeaderNames.EXPECT.equals(name) && !HttpHeaderNames.TRANSFER_ENCODING.equals(name) && !HttpHeaderNames.CONNECTION.equals(
				name))
			{
				connection.setRequestProperty(name, item.getValue());
			}
		}
	}

	private static void setContentHeaders(final HttpURLConnection connection, final HttpRequestMessage request)
	{
		final HttpContent content = request.getContent();
		if (content != null)
		{
			final Iterator<Map.Entry<String, String>> itr = content.getHeaders().getHeaderStrings();
			while (itr.hasNext())
			{
				final Map.Entry<String, String> item = itr.next();
				final String name = item.getKey();
				if (!HttpHeaderNames.CONTENT_LENGTH.equals(name))
				{
					connection.setRequestProperty(name, item.getValue());
				}
			}
		}
	}

	private static void initializeHttpURLConnection(final HttpURLConnection connection, final HttpRequestMessage request)
	{
	}

	private HttpResponseMessage createResponseMessage(final HttpURLConnection connection, final HttpRequestMessage request) throws IOException
	{
		final HttpResponseMessage response = new HttpResponseMessage();
		try
		{
			final int statusCode = connection.getResponseCode();
			response.setStatusCode(HttpStatusCode.fromInt(statusCode));

			final String statusDescription = connection.getResponseMessage();
			if (statusDescription != null && !statusDescription.isEmpty())
			{
				response.setReasonPhrase(statusDescription);
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

		final byte[] content = this.createResponseContent(connection, request);
		response.setContent(new ByteArrayContent(content));

		final Map<String, List<String>> headers = connection.getHeaderFields();
		for (final String name : headers.keySet())
		{
			if (!CONTENT_LENGTH.equalsIgnoreCase(name))
			{
				final List<String> valuesList = headers.get(name);
				final String[] values = valuesList.toArray(new String[valuesList.size()]);
				if (!response.getHeaders().tryAddWithoutValidation(name, values))
				{
					response.getContent().getHeaders().tryAddWithoutValidation(name, values);
				}
			}
		}

		request.setRequestUrl(connection.getURL());
		response.setRequestMessage(request);
		return response;
	}

	private byte[] createResponseContent(final HttpURLConnection connection, final HttpRequestMessage request) throws IOException
	{
		final InputStream inputStream = this.getStream(connection, request);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final byte[] buffer = new byte[0x2000];
		int i;
		while ((i = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, i);
		}
		outputStream.flush();
		return outputStream.toByteArray();
	}

	private InputStream getStream(final HttpURLConnection connection, final HttpRequestMessage request) throws IOException
	{
		final String contentEncoding = connection.getContentEncoding();
		if (this._automaticDecompression.contains(DecompressionMethods.GZip) && "gzip".equals(contentEncoding)) return new GZIPInputStream(connection.getInputStream());
		if (this._automaticDecompression.contains(DecompressionMethods.Deflate) && "deflate".equals(contentEncoding))
			return new InflaterInputStream(connection.getInputStream(), new Inflater(true));
		return connection.getInputStream();
	}

	public boolean isAllowAutoRedirect()
	{
		return this._allowAutoRedirect;
	}

	public void setAllowAutoRedirect(final boolean value)
	{
		this.checkClosed();
		this._allowAutoRedirect = value;
	}

	public EnumSet<DecompressionMethods> getAutomaticDecompression()
	{
		return this._automaticDecompression;
	}

	public void setAutomaticDecompression(final EnumSet<DecompressionMethods> value)
	{
		this.checkClosed();
		this._automaticDecompression = value;
	}

	public boolean isUseProxy()
	{
		return this._useProxy;
	}

	public void setUseProxy(final boolean value)
	{
		this.checkClosed();
		this._useProxy = value;
	}

	public Proxy getProxy()
	{
		return this._proxy;
	}

	public void setProxy(final Proxy value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException();
		}
		if (!this._useProxy)
		{
			throw new IllegalStateException();
		}
		this.checkClosed();
		this._proxy = value;
	}

	public boolean isUseCookie()
	{
		return this._useCookie;
	}

	public void setUseCookie(final boolean value)
	{
		this.checkClosed();
		this._useCookie = value;
	}

	public CookieManager getCookieContainer()
	{
		return this._cookieContainer;
	}

	public void setCookieContainer(final CookieManager value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException();
		}
		if (!this._useCookie)
		{
			throw new IllegalStateException();
		}
		this.checkClosed();
		this._cookieContainer = value;
	}
}