package net.mntone.httpclient;

import net.mntone.httpclient.headers.HttpContentHeaders;
import net.mntone.httpclient.headers.MediaTypeHeaderValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.Holder;

import jersey.repackaged.jsr166e.CompletableFuture;
import jersey.repackaged.jsr166e.CompletionException;

public abstract class HttpContent implements Closeable
{
	private static final List<Map.Entry<Charset, byte[]>> CHARSETS_AND_BOMS;

	protected static final Charset DEFAULT_CHARSET;

	static
	{
		if (!Charset.isSupported("UTF-8")) throw new RuntimeException();

		final Charset utf8Charset = Charset.forName("UTF-8");
		DEFAULT_CHARSET = utf8Charset;

		CHARSETS_AND_BOMS = new ArrayList<Map.Entry<Charset, byte[]>>();
		CHARSETS_AND_BOMS.add(new AbstractMap.SimpleImmutableEntry<Charset, byte[]>(utf8Charset, new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF }));
		if (Charset.isSupported("UTF-16LE"))
		{
			CHARSETS_AND_BOMS.add(new AbstractMap.SimpleImmutableEntry<Charset, byte[]>(Charset.forName("UTF-16LE"), new byte[] { (byte)0xFF, (byte)0xFE }));
		}
		if (Charset.isSupported("UTF-16BE"))
		{
			CHARSETS_AND_BOMS.add(new AbstractMap.SimpleImmutableEntry<Charset, byte[]>(Charset.forName("UTF-16BE"), new byte[] { (byte)0xFE, (byte)0xFF }));
		}
		if (Charset.isSupported("UTF-32LE"))
		{
			CHARSETS_AND_BOMS.add(new AbstractMap.SimpleImmutableEntry<Charset, byte[]>(Charset.forName("UTF-32LE"),
			                                                                            new byte[] { (byte)0xFF, (byte)0xFE, (byte)0x00, (byte)0x00 }));
		}
		if (Charset.isSupported("UTF-32BE"))
		{
			CHARSETS_AND_BOMS.add(new AbstractMap.SimpleImmutableEntry<Charset, byte[]>(Charset.forName("UTF-32BE"),
			                                                                            new byte[] { (byte)0x00, (byte)0x00, (byte)0xFE, (byte)0xFF }));
		}
	}

	private volatile boolean _closed = false;

	private boolean _canCalclateLength = true;
	private byte[] _bufferedContent = null;

	private HttpContentHeaders _headers = null;

	protected HttpContent()
	{ }

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

	private boolean isBuffered()
	{
		return this._bufferedContent != null;
	}

	private ByteArrayOutputStream createBufferedContent(final long maxBufferSize, final Holder<HttpRequestException> exception)
	{
		final Long contentLength = this._headers.getContentLength();
		if (contentLength == null) return new ByteArrayOutputStream((int)maxBufferSize);
		if (contentLength > maxBufferSize)
		{
			exception.value = new HttpRequestException();
			return null;
		}
		return new ByteArrayOutputStream(contentLength.intValue());
	}

	public CompletableFuture<Void> loadIntoBufferAsync()
	{
		return this.loadIntoBufferAsync(0x7fffffff);
	}

	public CompletableFuture<Void> loadIntoBufferAsync(final long maxBufferSize)
	{
		this.checkClosed();
		if (maxBufferSize > 0x7fffffff) throw new IllegalArgumentException();
		if (this.isBuffered()) return CompletableFuture.completedFuture(null);

		final Holder<HttpRequestException> exception = new Holder<HttpRequestException>();
		final ByteArrayOutputStream tempBuffer = this.createBufferedContent(maxBufferSize, exception);
		return CompletableFuture.runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				if (exception.value != null)
				{
					throw new CompletionException(exception.value.getMessage(), exception.value);
				}

				final CompletableFuture future = HttpContent.this.serializeToStreamAsync(tempBuffer);
				if (future == null) throw new CompletionException(null, new IllegalAccessException());
				try
				{
					future.get();
					if (future.isCancelled())
					{
						try
						{
							tempBuffer.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						throw new CompletionException(null);
					}

					final byte[] byteArray = tempBuffer.toByteArray();
					try
					{
						tempBuffer.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					HttpContent.this._bufferedContent = byteArray;
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					throw new CompletionException(e.getMessage(), e);
				}
				catch (ExecutionException e)
				{
					e.printStackTrace();
					throw new CompletionException(e.getMessage(), e);
				}
			}
		});
	}

	public CompletableFuture<InputStream> readAsInputStreamAsync()
	{
		this.checkClosed();
		return this.loadIntoBufferAsync().thenApplyAsync(new CompletableFuture.Fun<Void, InputStream>()
		{
			@Override
			public InputStream apply(final Void _)
			{
				return new ByteArrayInputStream(HttpContent.this._bufferedContent);
			}
		});
	}

	public CompletableFuture<byte[]> readAsByteArrayAsync()
	{
		this.checkClosed();
		return this.loadIntoBufferAsync().thenApplyAsync(new CompletableFuture.Fun<Void, byte[]>()
		{
			@Override
			public byte[] apply(final Void _)
			{
				return HttpContent.this._bufferedContent;
			}
		});
	}

	public CompletableFuture<String> readAsStringAsync()
	{
		return this.loadIntoBufferAsync().thenApplyAsync(new CompletableFuture.Fun<Void, String>()
		{
			@Override
			public String apply(final Void _)
			{
				final byte[] target = HttpContent.this._bufferedContent;
				if (target.length == 0) return "";

				int offset = -1;
				Charset charset = null;

				final MediaTypeHeaderValue contentType = HttpContent.this._headers.getContentType();
				if (contentType != null)
				{
					final String charset2 = contentType.getCharset();
					if (charset2 != null)
					{
						try
						{
							charset = Charset.forName(charset2);
						}
						catch (UnsupportedCharsetException e)
						{
							e.printStackTrace();
							throw new CompletionException(e.getMessage(), e);
						}
					}
				}

				if (charset == null)
				{
					for (final Map.Entry<Charset, byte[]> charsetAndBom : CHARSETS_AND_BOMS)
					{
						final byte[] preamble = charsetAndBom.getValue();
						if (startWithPrefix(target, preamble))
						{
							offset = preamble.length;
							charset = charsetAndBom.getKey();
							break;
						}
					}
				}
				if (charset == null) charset = DEFAULT_CHARSET;
				if (offset == -1)
				{
					final byte[] preamble = getRelatedBomByteArray(charset);
					offset = startWithPrefix(target, preamble) ? preamble.length : 0;
				}

				return new String(target, offset, target.length - offset, charset);
			}
		});
	}

	public CompletableFuture<String> readAsStringAsync(final Charset charset)
	{
		return this.loadIntoBufferAsync().thenApplyAsync(new CompletableFuture.Fun<Void, String>()
		{
			@Override
			public String apply(final Void _)
			{
				final byte[] target = HttpContent.this._bufferedContent;
				if (target.length == 0) return "";

				final byte[] preamble = getRelatedBomByteArray(charset);
				final int offset = startWithPrefix(target, preamble) ? preamble.length : 0;
				return new String(target, offset, target.length - offset, charset);
			}
		});
	}

	protected abstract CompletableFuture serializeToStreamAsync(final OutputStream stream);
	protected abstract Long tryComputeLength();

	private Long getComputeOrBufferLength()
	{
		this.checkClosed();
		if (this.isBuffered()) return (long)this._bufferedContent.length;
		if (this._canCalclateLength)
		{
			final Long result = this.tryComputeLength();
			if (result == null) this._canCalclateLength = false;
			return result;
		}
		return null;
	}

	public HttpContentHeaders getHeaders()
	{
		if (this._headers == null)
		{
			this._headers = new HttpContentHeaders(new HttpContentHeaders.LengthCalculator()
			{
				@Override
				public Long apply()
				{
					return HttpContent.this.getComputeOrBufferLength();
				}
			});
		}
		return this._headers;
	}

	protected void setContentType(final String mediaType)
	{
		final MediaTypeHeaderValue mediaTypeHeaderValue = new MediaTypeHeaderValue(mediaType);
		this.getHeaders().setContentType(mediaTypeHeaderValue);
	}

	protected void setContentType(final String mediaType, Charset charset)
	{
		if (charset == null) charset = DEFAULT_CHARSET;
		final String charsetName = charset.name();
		final MediaTypeHeaderValue mediaTypeHeaderValue = new MediaTypeHeaderValue(mediaType);
		mediaTypeHeaderValue.setCharset(charsetName);
		this.getHeaders().setContentType(mediaTypeHeaderValue);
	}

	private static byte[] getRelatedBomByteArray(final Charset targetCharset)
	{
		final String targetCharsetName = targetCharset.name();
		for (final Map.Entry<Charset, byte[]> charsetAndBom : CHARSETS_AND_BOMS)
		{
			final String charsetName = charsetAndBom.getKey().name();
			if (charsetName.equals(targetCharsetName))
			{
				return charsetAndBom.getValue();
			}
		}
		return new byte[] {};
	}

	private static boolean startWithPrefix(final byte[] byteArray, final byte[] prefix)
	{
		if (prefix.length > byteArray.length) return false;
		for (int i = 0; i < prefix.length; ++i)
		{
			if (prefix[i] != byteArray[i]) return false;
		}
		return true;
	}
}