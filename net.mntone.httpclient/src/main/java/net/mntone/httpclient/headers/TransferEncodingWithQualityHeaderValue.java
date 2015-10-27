package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

public final class TransferEncodingWithQualityHeaderValue extends TransferEncodingHeaderValueBase
{
	private static final String QUALITY = "q";

	TransferEncodingWithQualityHeaderValue()
	{ }

	protected TransferEncodingWithQualityHeaderValue(final TransferEncodingWithQualityHeaderValue source)
	{
		super(source);
	}

	public TransferEncodingWithQualityHeaderValue(final String value)
	{
		super(value);
	}

	@Override
	public TransferEncodingWithQualityHeaderValue clone()
	{
		return (TransferEncodingWithQualityHeaderValue)super.clone();
	}

	public final Double getQuality()
	{
		return super.getDouble(QUALITY);
	}
	public final void setQuality(final Double value)
	{
		super.setDouble(QUALITY, value, DOUBLE_0TO1_VALIDATOR);
	}

	public static TransferEncodingWithQualityHeaderValue parse(final String input)
	{
		return (TransferEncodingWithQualityHeaderValue)TransferEncodingWithQualityHeaderParser.SingleValueParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<TransferEncodingWithQualityHeaderValue> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!TransferEncodingWithQualityHeaderParser.SingleValueParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (TransferEncodingWithQualityHeaderValue)obj.value;
		return true;
	}
}