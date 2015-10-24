package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

public final class MediaTypeHeaderValue extends MediaTypeHeaderValueBase
{
	MediaTypeHeaderValue()
	{ }

	protected MediaTypeHeaderValue(final MediaTypeHeaderValue source)
	{
		super(source);
	}

	public MediaTypeHeaderValue(final String mediaType)
	{
		super(mediaType);
	}

	@Override
	public MediaTypeHeaderValue clone()
	{
		return (MediaTypeHeaderValue)super.clone();
	}

	public static MediaTypeHeaderValueBase parse(final String input)
	{
		return (MediaTypeHeaderValueBase)MediaTypeHeaderParser.SingleValueParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<MediaTypeHeaderValueBase> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!MediaTypeHeaderParser.SingleValueParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (MediaTypeHeaderValueBase)obj.value;
		return true;
	}
}