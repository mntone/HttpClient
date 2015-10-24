package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

public final class MediaTypeWithQualityHeaderValue extends MediaTypeHeaderValueBase
{
	private static final String QUALITY = "q";

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
		final NameValueHeaderValue nameValueHeaderValue = NameValueHeaderValue.find(this._parameters, QUALITY);
		if (nameValueHeaderValue == null) return null;

		try
		{
			final double quality = Double.parseDouble(nameValueHeaderValue.getValue());
			return quality;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public final void setQuality(final Double value)
	{
		final NameValueHeaderValue nameValueHeaderValue = NameValueHeaderValue.find(this._parameters, QUALITY);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (value < 0.0 || value > 1.0) throw new IllegalArgumentException();
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this.getParameters().add(new NameValueHeaderValue(QUALITY, value.toString()));
		}
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