package net.mntone.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public final class FormUrlEncodedContent extends ByteArrayContent
{
	public FormUrlEncodedContent(final Iterator<Map.Entry<String, String>> content) throws UnsupportedEncodingException
	{
		this(content, null);
	}

	public FormUrlEncodedContent(final Iterator<Map.Entry<String, String>> content, final Charset charset) throws UnsupportedEncodingException
	{
		super(getByteArrayContent(content, charset));

		this.setContentType("application/x-www-form-urlencoded", charset);
	}

	private static byte[] getByteArrayContent(final Iterator<Map.Entry<String, String>> content, Charset charset) throws UnsupportedEncodingException
	{
		if (content == null) throw new IllegalArgumentException();
		if (charset == null) charset = DEFAULT_CHARSET;

		final String encoding = charset.name();
		final StringBuilder builder = new StringBuilder();
		if (content.hasNext())
		{
			final Map.Entry<String, String> keyValuePair = content.next();
			setKeyValuePair(builder, keyValuePair, encoding);
		}
		while (content.hasNext())
		{
			final Map.Entry<String, String> keyValuePair = content.next();
			builder.append('&');
			setKeyValuePair(builder, keyValuePair, encoding);
		}

		return builder.toString().getBytes(charset);
	}

	private static void setKeyValuePair(final StringBuilder builder, final Map.Entry<String, String> keyValuePair, final String encoding) throws UnsupportedEncodingException
	{
		builder.append(URLEncoder.encode(keyValuePair.getKey(), encoding));
		builder.append('=');
		builder.append(URLEncoder.encode(keyValuePair.getValue(), encoding));
	}
}