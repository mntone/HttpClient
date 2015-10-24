package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

final class Int64NumberHeaderParser extends BaseHeaderParser
{
	public static final Int64NumberHeaderParser Instance = new Int64NumberHeaderParser();

	private Int64NumberHeaderParser()
	{
		super(false);
	}

	@Override
	protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
	{
		final int numberLength = HttpRuleParser.getNumberLength(value, startIndex, false);
		if (numberLength <= 0 || numberLength >= 19) return 0;

		final String numberText = value.substring(startIndex, numberLength);

		long number = 0;
		try
		{
			number = Long.parseLong(numberText);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		parsedValue.value = number;
		return numberLength;
	}
}