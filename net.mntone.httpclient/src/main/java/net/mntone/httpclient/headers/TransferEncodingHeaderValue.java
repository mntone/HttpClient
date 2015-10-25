package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

public final class TransferEncodingHeaderValue extends TransferEncodingHeaderValueBase
{
	TransferEncodingHeaderValue()
	{ }

	protected TransferEncodingHeaderValue(final TransferEncodingHeaderValue source)
	{
		super(source);
	}

	public TransferEncodingHeaderValue(final String value)
	{
		super(value);
	}

	@Override
	public TransferEncodingHeaderValue clone()
	{
		return (TransferEncodingHeaderValue)super.clone();
	}

	public static TransferEncodingHeaderValue parse(final String input)
	{
		return (TransferEncodingHeaderValue)TransferEncodingHeaderParser.SingleValueParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<MediaTypeHeaderValueBase> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!TransferEncodingHeaderParser.SingleValueParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (MediaTypeHeaderValueBase)obj.value;
		return true;
	}
}