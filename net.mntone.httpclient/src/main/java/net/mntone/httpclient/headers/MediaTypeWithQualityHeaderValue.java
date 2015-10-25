package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

public final class MediaTypeWithQualityHeaderValue extends MediaTypeHeaderValueBase
{
	MediaTypeWithQualityHeaderValue()
	{ }

	protected MediaTypeWithQualityHeaderValue(final MediaTypeWithQualityHeaderValue source)
	{
		super(source);
	}

	public MediaTypeWithQualityHeaderValue(final String mediaType)
	{
		super(mediaType);
	}

	public MediaTypeWithQualityHeaderValue(final String mediaType, final double quality)
	{
		super(mediaType);
	}

	@Override
	public MediaTypeWithQualityHeaderValue clone()
	{
		return (MediaTypeWithQualityHeaderValue)super.clone();
	}

	public final Double getQuality()
	{
		return HttpHeaderUtils.getQuality(this.getParameters());
	}
	public final void setQuality(final Double value)
	{
		HttpHeaderUtils.setQuality(this.getParameters(), value);
	}

	public static MediaTypeWithQualityHeaderValue parse(final String input)
	{
		return (MediaTypeWithQualityHeaderValue)MediaTypeWithQualityHeaderParser.SingleValueParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<MediaTypeWithQualityHeaderValue> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!MediaTypeWithQualityHeaderParser.SingleValueParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (MediaTypeWithQualityHeaderValue)obj.value;
		return true;
	}
}