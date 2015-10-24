package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

abstract class HttpHeaderParser
{
	private static final String DEFAULT_SEPARATOR = ", ";

	private final boolean _supportsMultipleValues;
	private final String _separator;

	protected HttpHeaderParser(final boolean supportsMultipleValues)
	{
		this._supportsMultipleValues = supportsMultipleValues;
		this._separator = DEFAULT_SEPARATOR;
	}

	protected HttpHeaderParser(final boolean supportsMultipleValues, final String separator)
	{
		this._supportsMultipleValues = supportsMultipleValues;
		this._separator = separator;
	}

	public abstract boolean tryParseValue(final String input, final Object storeValue, final Holder<Integer> index, final Holder<Object> parsedValue);

	public final boolean tryParseValue(final String input, final Holder<Object> parsedValue)
	{
		final Holder<Integer> index = new Holder<Integer>(0);
		return this.tryParseValue(input, null, index, parsedValue);
	}

	public final Object parseValue(final String input)
	{
		final Holder<Integer> index = new Holder<Integer>(0);
		return this.parseValue(input, null, index);
	}

	public final Object parseValue(final String input, final Object storeValue, final Holder<Integer> index)
	{
		final Holder<Object> result = new Holder<Object>();
		if (!this.tryParseValue(input, storeValue, index, result))
		{
			throw new IllegalArgumentException();
		}
		return result;
	}

	public String toString(final Object value)
	{
		return value.toString();
	}

	public final boolean isSupportsMultipleValues()
	{
		return this._supportsMultipleValues;
	}

	public final String getSeparator()
	{
		return this._separator;
	}

	public EqualityComparer getComparer()
	{
		return null;
	}

	public interface EqualityComparer
	{
		boolean equals(final Object x, final Object y);
	}
}