package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.ws.Holder;

public abstract class TransferEncodingHeaderValueBase implements Cloneable
{
	private String _value;
	ArrayList<NameValueHeaderValue> _parameters;

	TransferEncodingHeaderValueBase()
	{ }

	protected TransferEncodingHeaderValueBase(final TransferEncodingHeaderValueBase source)
	{
		this._value = source._value;
		if (this._parameters != null)
		{
			this._parameters = new ArrayList<NameValueHeaderValue>();
			for (final NameValueHeaderValue item : this._parameters)
			{
				this._parameters.add(item);
			}
		}
	}

	public TransferEncodingHeaderValueBase(final String value)
	{
		HttpHeaderUtils.checkValidToken(value);
		this._value = value;
	}

	@Override
	public TransferEncodingHeaderValueBase clone()
	{
		try
		{
			final TransferEncodingHeaderValueBase result = (TransferEncodingHeaderValueBase)super.clone();
			return result;
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof TransferEncodingHeaderValueBase)) return false;

		final TransferEncodingHeaderValueBase that = (TransferEncodingHeaderValueBase)obj;
		return this._value != null && this._value.equalsIgnoreCase(that._value) && NameValueHeaderValue.areEquals(this._parameters, that._parameters);
	}

	@Override
	public int hashCode()
	{
		return this._value.toLowerCase().hashCode() ^ NameValueHeaderValue.hashCode(this._parameters);
	}

	@Override
	public String toString()
	{
		return this._value + NameValueHeaderValue.toString(this._parameters, ';', true);
	}

	public final String getValue()
	{
		return this._value;
	}

	public final Collection<NameValueHeaderValue> getParameters()
	{
		if (this._parameters == null)
		{
			this._parameters = new ArrayList<NameValueHeaderValue>();
		}
		return this._parameters;
	}

	static <T extends TransferEncodingHeaderValueBase> int getTransferEncodingLength(final String input, final int startIndex, final Holder<T> parsedValue, Class<T> targetType)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;

		final int tokenLength = HttpRuleParser.getTokenLength(input, startIndex);
		if (tokenLength == 0) return 0;

		final String value = input.substring(startIndex, tokenLength);
		int i = startIndex + tokenLength;
		i += HttpRuleParser.getWhitespaceLength(input, i);

		if (i >= length || input.charAt(i) != ';')
		{
			try
			{
				final TransferEncodingHeaderValueBase transferEncodingHeaderValue = targetType.newInstance();
				transferEncodingHeaderValue._value = value;
				parsedValue.value = (T)transferEncodingHeaderValue;
				return i - startIndex;
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
				return 0;
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				return 0;
			}
		}
		++i;

		try
		{
			final TransferEncodingHeaderValueBase transferEncodingHeaderValue = targetType.newInstance();
			transferEncodingHeaderValue._value = value;

			final int nameValueListLength = NameValueHeaderValue.getNameValueCollectionLength(input,
			                                                                                  i,
			                                                                                  ';',
			                                                                                  transferEncodingHeaderValue.getParameters(),
			                                                                                  NameValueHeaderValue.class);
			if (nameValueListLength == 0) return 0;

			i += nameValueListLength;

			parsedValue.value = (T)transferEncodingHeaderValue;
			return i - startIndex;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return 0;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
}