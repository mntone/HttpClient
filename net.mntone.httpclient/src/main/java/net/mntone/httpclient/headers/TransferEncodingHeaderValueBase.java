package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

public abstract class TransferEncodingHeaderValueBase extends HttpParameterValueBase
{
	private String _value;

	TransferEncodingHeaderValueBase()
	{ }

	protected TransferEncodingHeaderValueBase(final TransferEncodingHeaderValueBase source)
	{
		super(source);
		this._value = source._value;
	}

	public TransferEncodingHeaderValueBase(final String value)
	{
		HttpHeaderUtils.checkValidToken(value);
		this._value = value;
	}

	@Override
	public TransferEncodingHeaderValueBase clone()
	{
		return (TransferEncodingHeaderValueBase)super.clone();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof TransferEncodingHeaderValueBase)) return false;

		final TransferEncodingHeaderValueBase that = (TransferEncodingHeaderValueBase)obj;
		return this._value != null && this._value.equalsIgnoreCase(that._value) && super.equalsCollection(that.getParameters());
	}

	@Override
	public int hashCode()
	{
		return this._value.toLowerCase().hashCode() ^ super.hashCode();
	}

	@Override
	public String toString()
	{
		return this._value + super.toString();
	}

	public final String getValue()
	{
		return this._value;
	}

	static <T extends TransferEncodingHeaderValueBase> int getTransferEncodingLength(final String input, final Holder<Integer> index, final Holder<T> parsedValue, final Class<T> targetType)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final int tokenLength = HttpRuleParser.getTokenLength(input, index2);
		if (tokenLength == 0) return 0;
		final String value = input.substring(index.value, index2.value);

		HttpRuleParser.getWhitespaceLength(input, index2);

		if (index2.value >= length || input.charAt(index2.value) != ';')
		{
			try
			{
				final TransferEncodingHeaderValueBase transferEncodingHeaderValue = targetType.newInstance();
				transferEncodingHeaderValue._value = value;
				parsedValue.value = (T)transferEncodingHeaderValue;

				final int a = index2.value - index.value;
				index.value = index2.value;
				return a;
			}
			catch (final InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (final IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return 0;
		}
		++index2.value;

		try
		{
			final TransferEncodingHeaderValueBase transferEncodingHeaderValue = targetType.newInstance();
			transferEncodingHeaderValue._value = value;

			final int nameValueListLength = NameValueHeaderValue.getNameValueCollectionLength(
				input,
				index2,
				';',
				transferEncodingHeaderValue.getParameters(),
				NameValueHeaderValue.class);
			if (nameValueListLength == 0) return 0;
			parsedValue.value = (T)transferEncodingHeaderValue;

			final int a = index2.value - index.value;
			index.value = index2.value;
			return a;
		}
		catch (final InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}