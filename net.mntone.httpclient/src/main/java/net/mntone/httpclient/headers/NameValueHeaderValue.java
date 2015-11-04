package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpParseResult;
import net.mntone.httpclient.HttpRuleParser;

import java.util.Collection;

import javax.xml.ws.Holder;

public class NameValueHeaderValue implements Cloneable
{
	private String _name;
	private String _value;

	NameValueHeaderValue()
	{ }

	protected NameValueHeaderValue(final NameValueHeaderValue source)
	{
		this._name = source._name;
		this._value = source._value;
	}

	public NameValueHeaderValue(final String name)
	{
		this(name, null);
	}

	public NameValueHeaderValue(final String name, final String value)
	{
		this._name = name;
		this._value = value;
	}

	@Override
	public NameValueHeaderValue clone()
	{
		try
		{
			final NameValueHeaderValue result = (NameValueHeaderValue)super.clone();
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
		if (!(obj instanceof NameValueHeaderValue)) return false;

		final NameValueHeaderValue that = (NameValueHeaderValue)obj;
		if (this._name.equalsIgnoreCase(that._name)) return false;
		if (this._value == null || this._value.isEmpty())
		{
			return that._value == null || that._value.isEmpty();
		}

		if (this._value.charAt(0) == '"') return this._value.equals(that._value);
		return this._value.equalsIgnoreCase(that._value);
	}

	static boolean areEquals(final Collection<NameValueHeaderValue> x, final Collection<NameValueHeaderValue> y)
	{
		return HttpHeaderUtils.equalsCollection(x, y);
	}

	@Override
	public int hashCode()
	{
		final int hashCode = this._name.toLowerCase().hashCode();
		if (this._value == null || this._value.isEmpty()) return hashCode;

		if (this._value.charAt(0) == '"') return hashCode ^ this._value.hashCode();
		return hashCode ^ this._value.toLowerCase().hashCode();
	}

	static int hashCode(final Collection<NameValueHeaderValue> collection)
	{
		if (collection == null || collection.size() == 0) return 0;

		int hashCode = 0;
		for (final NameValueHeaderValue item : collection)
		{
			hashCode ^= item.hashCode();
		}
		return hashCode;
	}

	@Override
	public String toString()
	{
		if (this._value == null || this._value.isEmpty()) return this._name;
		return this._name + "=" + this._value;
	}

	static String toString(final Collection<NameValueHeaderValue> collection, final char delimiter)
	{
		final StringBuilder builder = new StringBuilder();
		appendString(builder, collection, delimiter, false);
		return builder.toString();
	}

	static String toString(final Collection<NameValueHeaderValue> collection, final char delimiter, final boolean leadingSeparator)
	{
		final StringBuilder builder = new StringBuilder();
		appendString(builder, collection, delimiter, leadingSeparator);
		return builder.toString();
	}

	static void appendString(final StringBuilder builder, final Collection<NameValueHeaderValue> collection, final char delimiter, final boolean leadingSeparator)
	{
		for (final NameValueHeaderValue item : collection)
		{
			if (leadingSeparator || builder.length() > 0)
			{
				builder.append(delimiter);
				builder.append(' ');
			}
			builder.append(item.toString());
		}
	}

	public String getName()
	{
		return this._name;
	}

	public String getValue()
	{
		return this._value;
	}

	public void setValue(final String value)
	{
		this._value = value;
	}

	static <T extends NameValueHeaderValue> int getNameValueLength(final String input, final Holder<Integer> index, final Holder<T> parsedValue, final Class<T> targetType)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final int nameLength = HttpRuleParser.getTokenLength(input, index2);
		if (nameLength == 0) return 0;
		final String name = input.substring(index.value, index2.value);

		HttpRuleParser.getWhitespaceLength(input, index2);

		if (index2.value >= length || input.charAt(index2.value) != '=')
		{
			try
			{
				final NameValueHeaderValue nameValueHeaderValue = targetType.newInstance();
				nameValueHeaderValue._name = name;
				parsedValue.value = (T)nameValueHeaderValue;

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
		HttpRuleParser.getWhitespaceLength(input, index2);

		final int valueIndex = index2.value;
		final int valueLength = getValueLength(input, index2);
		if (valueLength == 0) return 0;
		final String value = input.substring(valueIndex, index2.value);

		HttpRuleParser.getWhitespaceLength(input, index2);

		try
		{
			final NameValueHeaderValue nameValueHeaderValue = targetType.newInstance();
			nameValueHeaderValue._name = name;
			nameValueHeaderValue._value = value;
			parsedValue.value = (T)nameValueHeaderValue;

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

	static <T extends NameValueHeaderValue> int getNameValueCollectionLength(
		final String input, final Holder<Integer> index, final char delimiter, final Collection<T> nameValueCollection, final Class<T> targetType)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		HttpRuleParser.getWhitespaceLength(input, index2);
		while (true)
		{
			final Holder<T> item = new Holder<T>();
			final int nameValueLength = NameValueHeaderValue.getNameValueLength(input, index2, item, targetType);
			if (nameValueLength == 0) return 0;
			nameValueCollection.add(item.value);

			HttpRuleParser.getWhitespaceLength(input, index2);

			if (index2.value == length || input.charAt(index2.value) != delimiter) break;
			++index2.value;

			HttpRuleParser.getWhitespaceLength(input, index2);
		}

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}

	private static int getValueLength(final String input, final Holder<Integer> index)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final Holder<Integer> tokenLength = new Holder<Integer>(HttpRuleParser.getTokenLength(input, index2));
		if (tokenLength.value == 0)
		{
			if (HttpRuleParser.getQuotedStringLength(input, index.value, tokenLength) != HttpParseResult.Parsed) return 0;
		}
		index.value += tokenLength.value;
		return tokenLength.value;
	}

	@SuppressWarnings("Contract")
	static NameValueHeaderValue find(final Collection<NameValueHeaderValue> collection, final String name)
	{
		if (collection == null) return null;
		for (final NameValueHeaderValue item : collection)
		{
			if (name.equalsIgnoreCase(item._name))
			{
				return item;
			}
		}
		return null;
	}

	private static void checkNameValueFormat(final String name, final String value)
	{
		HttpHeaderUtils.checkValidToken(name);
		checkValueFormat(value);
	}

	private static void checkValueFormat(final String value)
	{
		final Holder<Integer> index = new Holder<Integer>(0);
		if (value != null && !value.isEmpty() && getValueLength(value, index) != value.length())
		{
			throw new IllegalArgumentException();
		}
	}
}