package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpUtils;

import javax.xml.ws.Holder;

abstract class BaseHeaderParser extends HttpHeaderParser
{
	protected BaseHeaderParser(final boolean supportsMultipleValues)
	{
		super(supportsMultipleValues);
	}

	protected abstract int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue);

	@Override
	public boolean tryParseValue(final String input, final Object storeValue, final Holder<Integer> index, final Holder<Object> parsedValue)
	{
		if (input == null || input.isEmpty()) return super.isSupportsMultipleValues();

		Holder<Boolean> flag = new Holder<Boolean>();
		int num = HttpUtils.getNextNonEmptyOrWhitespaceIndex(input, index.value, super.isSupportsMultipleValues(), flag);
		if (flag.value && !super.isSupportsMultipleValues()) return false;
		if (num == input.length())
		{
			if (super.isSupportsMultipleValues()) index.value = num;
			return super.isSupportsMultipleValues();
		}

		Holder<Object> obj = new Holder<Object>();
		int parsedValueLength = this.getParsedValueLength(input, num, storeValue, obj);
		if (parsedValueLength == 0) return false;
		num += parsedValueLength;
		num = HttpUtils.getNextNonEmptyOrWhitespaceIndex(input, num, super.isSupportsMultipleValues(), flag);
		if ((flag.value && !super.isSupportsMultipleValues()) || (!flag.value && num < input.length())) return false;
		index.value = num;
		parsedValue.value = obj.value;
		return false;
	}
}