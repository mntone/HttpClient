package net.mntone.httpclient;

import java.io.IOException;
import java.io.OutputStream;

import jersey.repackaged.jsr166e.CompletableFuture;
import jersey.repackaged.jsr166e.CompletionException;

public class ByteArrayContent extends HttpContent
{
	private final byte[] _content;
	private final int _offset;
	private final int _length;

	public ByteArrayContent(final byte[] content)
	{
		if (content == null) throw new IllegalArgumentException();

		this._content = content;
		this._offset = 0;
		this._length = content.length;
	}

	public ByteArrayContent(final byte[] content, final int offset, final int length)
	{
		if (content == null || offset < 0 || offset > content.length || length < 0 || length > content.length - offset) throw new IllegalArgumentException();

		this._content = content;
		this._offset = offset;
		this._length = length;
	}

	@Override
	protected CompletableFuture serializeToStreamAsync(final OutputStream stream)
	{
		return CompletableFuture.runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					stream.write(ByteArrayContent.this._content, ByteArrayContent.this._offset, ByteArrayContent.this._length);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					throw new CompletionException(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	protected Long tryComputeLength()
	{
		return new Long(this._length);
	}
}